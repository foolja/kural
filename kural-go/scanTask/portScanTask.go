package scanTask

import (
	"fmt"
	"github.com/google/gopacket"
	"github.com/google/gopacket/layers"
	"kural/config"
	"kural/webSocket"
	"net"
	"sync"
	"sync/atomic"
	"time"
)

type SingleTargetPortInfo struct {
	Port           int    `json:"port"`
	ServiceType    string `json:"serviceType"`
	ServiceVersion string `json:"serviceVersion"`
}

var (
	ResultForPortScan sync.Map
)

func GeneratePortScanTask(ipList []net.IP, portList []int) ([]map[string]int, int) {
	tasks := make([]map[string]int, 0)
	for _, ip := range ipList {
		for _, port := range portList {
			ipPort := map[string]int{ip.String(): port}
			tasks = append(tasks, ipPort)
		}
	}
	return tasks, len(tasks)
}

func RunPortScanTask(tasks []map[string]int, taskId string) {
	total := len(tasks)
	var current int32 // 原子计数器
	wg := &sync.WaitGroup{}

	// 创建一个buffer为vars.threadNum * 2的channel
	taskChan := make(chan map[string]int, config.ThreadNum*2)

	// 创建config.ThreadNum个协程
	for i := 0; i < config.ThreadNum; i++ {
		go func() {
			for task := range taskChan {
				for ip, port := range task {
					err := SavePortScanResult(config.ConnectProxy(ip, port))
					if err != nil {

					}
					atomic.AddInt32(&current, 1)
					webSocket.ProgressExecutor(int(atomic.LoadInt32(&current)), total, taskId)
					wg.Done()
				}
			}
		}()
	}
	// 生产者，不断地往taskChan channel发送数据，直到channel阻塞
	for _, task := range tasks {
		wg.Add(1)
		taskChan <- task
	}
	close(taskChan)
	wg.Wait()
}

func SavePortScanResult(ip string, port int, err error) error {
	if err != nil || port <= 0 {
		return err // 错误或无效端口直接返回
	}

	// 尝试获取或初始化该 IP 的端口列表
	value, loaded := ResultForPortScan.LoadOrStore(ip, []int{port})

	// 将 value 转换为 []int 类型
	ports, ok := value.([]int)
	if !ok {
		return nil // 处理类型错误（此处可根据实际情况调整）
	}

	// 如果已存在条目（loaded=true），追加前需检查是否重复
	if loaded {
		found := false
		for _, p := range ports {
			if p == port {
				found = true
				break
			}
		}
		if !found {
			// 重新创建一个新切片，避免影响当前已被其他 goroutine 读取的值
			newPorts := append(ports, port)
			ResultForPortScan.Store(ip, newPorts) // 覆盖存储确保追加操作
		}
	}

	return nil
}

// 通过UDP连接获取本机在连接目标主机时的IP地址
func GetlocalIPPort(destinationIP net.IP) (net.IP, int, error) {
	serverAddr, err := net.ResolveUDPAddr("udp", destinationIP.String()+":54321")
	if err != nil {
		return nil, 0, err
	}

	// 尝试连接目标地址的 54321 端口
	if con, err := net.DialUDP("udp", nil, serverAddr); err == nil {
		if udpaddr, ok := con.LocalAddr().(*net.UDPAddr); ok {
			return udpaddr.IP, udpaddr.Port, nil
		}
	}

	// 如果连接失败，返回错误
	return nil, -1, fmt.Errorf("failed to connect to %s", destinationIP.String()+":54321")
}
func Connect(ip string, port int) (string, int, error) {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("%v:%v", ip, port), 2*time.Second)

	defer func() {
		if conn != nil {
			_ = conn.Close()
		}
	}()

	return ip, port, err
}
func HalfConnect(dstIp string, dstPort int) (string, int, error) {
	srcIp, srcPort, err := GetlocalIPPort(net.ParseIP(dstIp))
	dstAddrs, err := net.LookupIP(dstIp)
	if err != nil {
		return dstIp, 0, err
	}

	dstip := dstAddrs[0].To4()
	var dstport layers.TCPPort
	dstport = layers.TCPPort(dstPort)
	srcport := layers.TCPPort(srcPort)

	// Our IP header... not used, but necessary for TCP checksumming.
	ip := &layers.IPv4{
		SrcIP:    srcIp,
		DstIP:    dstip,
		Protocol: layers.IPProtocolTCP,
	}
	// Our TCP header
	tcp := &layers.TCP{
		SrcPort: srcport,
		DstPort: dstport,
		SYN:     true,
	}
	err = tcp.SetNetworkLayerForChecksum(ip)

	buf := gopacket.NewSerializeBuffer()
	opts := gopacket.SerializeOptions{
		ComputeChecksums: true,
		FixLengths:       true,
	}

	if err := gopacket.SerializeLayers(buf, opts, tcp); err != nil {
		return dstIp, 0, err
	}

	conn, err := net.ListenPacket("ip4", "0.0.0.0")
	if err != nil {
		return dstIp, 0, err
	}
	defer conn.Close()

	if _, err := conn.WriteTo(buf.Bytes(), &net.IPAddr{IP: dstip}); err != nil {
		return dstIp, 0, err
	}

	// Set deadline so we don't wait forever.
	if err := conn.SetDeadline(time.Now().Add(time.Duration(config.TimeOutForTCP) * time.Second)); err != nil {
		return dstIp, 0, err
	}

	for {
		b := make([]byte, 4096)
		n, addr, err := conn.ReadFrom(b)
		if err != nil {
			return dstIp, 0, err
		} else if addr.String() == dstip.String() {
			// Decode a packet
			packet := gopacket.NewPacket(b[:n], layers.LayerTypeTCP, gopacket.Default)
			// Get the TCP layer from this packet
			if tcpLayer := packet.Layer(layers.LayerTypeTCP); tcpLayer != nil {
				tcp, _ := tcpLayer.(*layers.TCP)

				if tcp.DstPort == srcport {
					if tcp.SYN && tcp.ACK {

						return dstIp, dstPort, err
					} else {
						return dstIp, 0, err
					}
				}
			}
		}
	}
}
