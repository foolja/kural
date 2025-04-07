package scanTask

import (
	"context"
	"fmt"
	"golang.org/x/crypto/ssh"
	"kural/config"
	"kural/webSocket"
	"log"
	"net"
	"sync"
	"sync/atomic"
)

// WeakPasswordTarget 定义目标信息
type WeakPasswordTarget struct {
	IP       string
	Port     int
	Service  string
	Username string
}

// WeakPasswordScanTask 扫描任务结构
type WeakPasswordScanTask struct {
	Target   WeakPasswordTarget
	Password string
}
type SingleTargetWeakPasswordScanInfo struct {
	Port     int    `json:"port"`
	Service  string `json:"service"`
	Username string `json:"username"`
	Password string `json:"password"`
}

// TargetContext 保存目标上下文和取消函数
type TargetContext struct {
	Ctx    context.Context
	Cancel context.CancelFunc
}

var (
	contexts              sync.Map // 存储目标上下文，键为targetKey，值为*TargetContext
	targetLocks           sync.Map // 用于确保同一目标(WeakPasswordTarget)不同时被多个协程扫描,key: targetKey (string), value: *sync.Mutex
	targetSemaphores      sync.Map // 用 信号量（Semaphore）替代互斥锁，控制每个目标的并发数,key: targetKey, value: chan struct{}
	ResultForWeakPassword sync.Map
)

func RunWeakPassWordTask(targets []WeakPasswordTarget, passwords []string, taskId string) {
	taskCh := GenerateWeakPasswordScanTasks(targets, passwords)

	total := len(targets) * len(passwords)
	var current int32
	// 启动worker池
	var wg sync.WaitGroup

	for i := 0; i < config.ThreadNum; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			worker(taskCh, &current, total, taskId)
		}()
	}

	// 结果收集
	wg.Wait()
}

// generateTasks 生成扫描任务，发送到任务通道
func GenerateWeakPasswordScanTasks(targets []WeakPasswordTarget, passwords []string) chan WeakPasswordScanTask {
	taskCh := make(chan WeakPasswordScanTask, config.ThreadNum)
	go func() {
		defer close(taskCh)
		for _, target := range targets {
			targetKey := getTargetKey(target)
			// 获取或创建目标上下文
			tc := loadOrCreateTargetContext(targetKey)
			// 生成密码任务
		passwordLoop:
			for _, password := range passwords {
				select {
				case <-tc.Ctx.Done(): // 检查是否已取消
					log.Printf("取消掉了")
					break passwordLoop
				default:
					taskCh <- WeakPasswordScanTask{Target: target, Password: password}
				}
			}
		}
	}()
	return taskCh
}

// getTargetKey 生成目标的唯一键
func getTargetKey(t WeakPasswordTarget) string {
	return fmt.Sprintf("%s:%d:%s:%s", t.IP, t.Port, t.Service, t.Username)
}

// loadOrCreateTargetContext 加载或创建目标的上下文
func loadOrCreateTargetContext(targetKey string) *TargetContext {
	actual, loaded := contexts.Load(targetKey)
	if loaded {
		return actual.(*TargetContext)
	}
	// 创建新的上下文和取消函数
	ctx, cancel := context.WithCancel(context.Background())
	tc := &TargetContext{Ctx: ctx, Cancel: cancel}
	contexts.Store(targetKey, tc)
	return tc
}

// worker 处理扫描任务
func worker(taskCh chan WeakPasswordScanTask, current *int32, total int, taskId string) {
	for task := range taskCh {
		targetKey := getTargetKey(task.Target)
		atomic.AddInt32(current, 1)
		webSocket.ProgressExecutor(int(atomic.LoadInt32(current)), total, taskId)
		// 第一步：快速检查是否已取消（无需加锁）
		tcInterface, ok := contexts.Load(targetKey)
		if !ok {
			continue // 目标上下文已不存在（可能已被取消并清理）
		}
		tc := tcInterface.(*TargetContext)
		select {
		case <-tc.Ctx.Done():
			continue // 已取消，跳过任务
		default:
		}

		// 第二步：获取目标信号量（允许最多5个并发）
		sem, _ := targetSemaphores.LoadOrStore(targetKey, make(chan struct{}, 5))
		semChan := sem.(chan struct{})
		semChan <- struct{}{} // 阻塞直到成功放入信号量

		// 再次检查是否已取消
		tcInterface, ok = contexts.Load(targetKey)
		if !ok {
			<-semChan
			return
		}
		tc = tcInterface.(*TargetContext)
		select {
		case <-tc.Ctx.Done():
			<-semChan
			return
		default:
		}

		if task.Target.Service == "SSH" {
			success := ScanSsh(task.Target, task.Password)

			<-semChan
			log.Printf("尝试 username:%s, password:%s", task.Target.Username, task.Password)
			if success {
				ResultForWeakPassword.Store(task.Target, task.Password)
				log.Printf("爆破成功: username:%s, password:%s", task.Target.Username, task.Password)
				tc.Cancel()
			}
		}

	}
}
func ScanSsh(target WeakPasswordTarget, password string) (result bool) {

	sshConfig := &ssh.ClientConfig{
		User: target.Username,
		Auth: []ssh.AuthMethod{
			ssh.Password(password),
		},
		Timeout: config.TimeOutForSSH,
		HostKeyCallback: func(hostname string, remote net.Addr, key ssh.PublicKey) error {
			//验证公钥
			return nil
		},
	}

	client, err := ssh.Dial("tcp", fmt.Sprintf("%v:%v", target.IP, target.Port), sshConfig)
	if err != nil {
		if password == "root" {
			log.Printf("%v", err)
		}
		return false
	}

	session, err := client.NewSession()
	if err != nil {
		return false
	}
	err = session.Run("echo 666")
	if err != nil {
		return false
	}

	defer func() {
		if client != nil {
			_ = client.Close()
		}
		if session != nil {
			_ = session.Close()
		}
	}()
	return true
}
