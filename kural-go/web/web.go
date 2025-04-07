package web

import (
	"bytes"
	"encoding/json"
	"github.com/gin-gonic/gin"
	"kural/config"
	"kural/scanTask"
	"kural/utils"
	"kural/webSocket"
	"log"
	"net/http"
	"strings"
	"sync"
)

type PortScanTaskRequest struct {
	TaskID   string `json:"taskId"`
	TaskType string `json:"taskType"`
	Target   string `json:"target"`
	Priority int    `json:"priority"`

	PortList   string `json:"portList,omitempty"` // PortScanTask 特有字段
	IsFullScan bool   `json:"isFullScan,omitempty"`
	ScanMethod string `json:"scanMethod,omitempty"`
}
type PortScanResultResponse struct {
	TaskID string                                     `json:"taskId"`
	Result map[string][]scanTask.SingleTargetPortInfo `json:"result"`
}
type WeakPasswordRequest struct {
	TaskID   string `json:"taskId"`
	TaskType string `json:"taskType"`
	Target   string `json:"target"`
	Priority int    `json:"priority"`

	ServiceTypes     string `json:"serviceTypes,omitempty"`
	UsernameFilePath string `json:"usernameFilePath,omitempty"`
	PasswordFilePath string `json:"passwordFilePath,omitempty"`
	UseDefaultDict   bool   `json:"useDefaultDict,omitempty"`
}
type WeakPasswordScanResultResponse struct {
	TaskId string                                                 `json:"taskId"`
	Result map[string][]scanTask.SingleTargetWeakPasswordScanInfo `json:"result"`
}

type WebVulnScanTaskRequest struct {
	TaskID   string `json:"taskId"`
	TaskType string `json:"taskType"`
	Target   string `json:"target"`
	Priority int    `json:"priority"`

	PocContent string `json:"pocContent"`
}

type WebVulnScanResultResponse struct {
	TaskId string                                            `json:"taskId"`
	Result map[string][]scanTask.SingleTargetWebVulnScanInfo `json:"result"`
}

func HandlePortScanRequest(c *gin.Context) {
	// 校验身份（例如 API Key）
	apiKey := c.GetHeader("X-API-Key")
	if apiKey != "a3VyYWw=" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "未授权的访问"})
		return
	}

	// 解析请求体
	var req PortScanTaskRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "无效的请求体"})
		return
	}
	log.Printf("开始执行扫描任务: TaskID=%s", req.TaskID)
	log.Printf("开始执行扫描任务: TaskType=%s", req.TaskType)
	log.Printf("开始执行扫描任务: WeakPasswordTarget=%s", req.Target)
	// 异步启动扫描任务（不阻塞 HTTP 请求）
	go func() {
		// Step 1: 初始化扫描任务
		log.Printf("开始执行扫描任务: TaskID=%s", req.TaskID)
		if req.ScanMethod == "TCP连接" {
			config.ConnectProxy = scanTask.Connect
		} else if req.ScanMethod == "SYN扫描" {
			config.ConnectProxy = scanTask.HalfConnect
		}
		ips, err := utils.GetIPList(req.Target)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "ip地址格式错误"})
			return
		}
		ports, err := utils.GetPortList(req.PortList)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "端口格式错误"})
			return
		}
		tasks, _ := scanTask.GeneratePortScanTask(ips, ports)
		scanTask.RunPortScanTask(tasks, req.TaskID)
		webSocket.LastProgress[req.TaskID] = 0
		webSocket.CloseTaskConnections(req.TaskID)

		SendPortScanResultToSpringBoot(req.TaskID)
	}()

	// 立即返回响应
	c.JSON(http.StatusOK, gin.H{
		"status":  "accepted",
		"taskId":  req.TaskID,
		"message": "任务已接收，正在后台执行",
	})
}

func SendPortScanResultToSpringBoot(taskId string) {
	// Step 1: 将 config.Result 转换为适当的结构
	resultData := make(map[string][]scanTask.SingleTargetPortInfo)

	// 遍历 config.Result，将数据放入一个新的 map 中
	scanTask.ResultForPortScan.Range(func(key, value interface{}) bool {
		if strKey, ok := key.(string); ok {
			if intArrayValue, ok := value.([]int); ok {
				// 为每个端口生成一个 PortInfo 对象，并设置 ServiceType 和 ServiceVersion 为 null
				var portInfoList []scanTask.SingleTargetPortInfo
				for _, port := range intArrayValue {
					portInfoList = append(portInfoList, scanTask.SingleTargetPortInfo{
						Port:           port,
						ServiceType:    "", // 默认设置为 null 或空字符串
						ServiceVersion: "", // 默认设置为 null 或空字符串
					})
				}
				resultData[strKey] = portInfoList
			}
		}
		return true
	})

	// Step 2: 将 taskId 和结果数据包装成新的结构体
	portScanResult := PortScanResultResponse{
		TaskID: taskId,
		Result: resultData,
	}

	// Step 3: 将结果数据序列化为 JSON 格式
	jsonData, err := json.Marshal(portScanResult)
	if err != nil {
		log.Printf("JSON 序列化失败: %v", err)
		return
	}

	// Step 4: 创建 HTTP POST 请求
	url := "http://127.0.0.1:8888/api/v1/task/portScanResult"
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		log.Printf("创建 HTTP 请求失败: %v", err)
		return
	}
	req.Header.Set("Content-Type", "application/json")

	// Step 5: 发送请求并处理响应
	client := &http.Client{}
	resp, err := client.Do(req)
	log.Printf("任务%s扫描结果已发送到SpringBoot", taskId)
	if err != nil {
		log.Printf("发送 HTTP 请求失败: %v", err)
		return
	}
	defer resp.Body.Close()

	// 检查响应状态码
	if resp.StatusCode != http.StatusOK {
		log.Printf("请求失败，状态码: %d", resp.StatusCode)
		return
	}
	// Step 6: 清空 scanTask.ResultForPortScan
	scanTask.ResultForPortScan = sync.Map{}
}

func HandleWeakPasswordRequest(c *gin.Context) {
	// 校验身份（例如 API Key）
	apiKey := c.GetHeader("X-API-Key")
	if apiKey != "a3VyYWw=" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "未授权的访问"})
		return
	}

	// 解析请求体
	var req WeakPasswordRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "无效的请求体"})
		return
	}
	log.Printf("开始执行扫描任务: TaskID=%s", req.TaskID)
	log.Printf("开始执行扫描任务: TaskType=%s", req.TaskType)
	log.Printf("开始执行扫描任务: WeakPasswordTarget=%s", req.Target)
	go func() {
		ips, err := utils.GetIPList(req.Target)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "ip地址格式错误"})
			return
		}
		serviceTypeList := strings.Split(req.ServiceTypes, ",")
		var username []string
		var password []string
		var errForFile error
		if req.UsernameFilePath == "" {
			username, errForFile = utils.ReadUsernameFile("dictionary/haha.txt", true, true)
		} else {
			username, errForFile = utils.ReadUsernameFile(req.UsernameFilePath, true, true)
		}
		if errForFile != nil {
			log.Printf("读取用户名文件失败: %v", errForFile)
			c.JSON(http.StatusBadRequest, gin.H{"error": errForFile})
			return
		}
		if req.PasswordFilePath == "" {
			password, errForFile = utils.ReadPasswordFile("dictionary/default_password.txt", true, true)
		} else {
			password, errForFile = utils.ReadPasswordFile(req.PasswordFilePath, true, true)
		}
		if errForFile != nil {
			log.Printf("读取用户名文件失败: %v", errForFile)
			c.JSON(http.StatusBadRequest, gin.H{"error": errForFile})
			return
		}
		aliveTargets := utils.CheckAlive(ips, serviceTypeList)
		if len(aliveTargets) == 0 {
			log.Printf("任务%s暂无扫描目标", req.TaskID)
			webSocket.BroadcastProgress(req.TaskID, 100)
			return
		}
		targets := utils.ConvertToTargets(aliveTargets, username)

		scanTask.RunWeakPassWordTask(targets, password, req.TaskID)
		webSocket.LastProgress[req.TaskID] = 0
		webSocket.CloseTaskConnections(req.TaskID)

		SendWeakPasswordScanResultToSpringBoot(req.TaskID)
	}()
	// 立即返回响应
	c.JSON(http.StatusOK, gin.H{
		"status":  "accepted",
		"taskId":  req.TaskID,
		"message": "任务已接收，正在后台执行",
	})
}

func SendWeakPasswordScanResultToSpringBoot(taskId string) {
	response := WeakPasswordScanResultResponse{
		TaskId: taskId,
		Result: make(map[string][]scanTask.SingleTargetWeakPasswordScanInfo),
	}
	// 遍历 sync.Map
	scanTask.ResultForWeakPassword.Range(func(key, value interface{}) bool {
		// 类型断言：key必须是Target，value必须是string
		target, ok := key.(scanTask.WeakPasswordTarget)
		if !ok {
			return true // 跳过无效的key
		}
		password, ok := value.(string)
		if !ok {
			return true // 跳过无效的value
		}

		// 构建扫描信息对象
		scanInfo := scanTask.SingleTargetWeakPasswordScanInfo{
			Port:     target.Port,
			Service:  target.Service,
			Username: target.Username,
			Password: password,
		}

		// 按IP分组存储
		ip := target.IP
		if _, exists := response.Result[ip]; !exists {
			response.Result[ip] = []scanTask.SingleTargetWeakPasswordScanInfo{}
		}
		response.Result[ip] = append(response.Result[ip], scanInfo)

		return true
	})
	// Step 3: 将结果数据序列化为 JSON 格式
	jsonData, err := json.Marshal(response)
	if err != nil {
		log.Printf("JSON 序列化失败: %v", err)
		return
	}

	// Step 4: 创建 HTTP POST 请求
	url := "http://127.0.0.1:8888/api/v1/task/weakPasswordScanResult"
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		log.Printf("创建 HTTP 请求失败: %v", err)
		return
	}
	req.Header.Set("Content-Type", "application/json")

	// Step 5: 发送请求并处理响应
	client := &http.Client{}
	resp, err := client.Do(req)
	log.Printf("任务%s扫描结果已发送到SpringBoot", taskId)
	if err != nil {
		log.Printf("发送 HTTP 请求失败: %v", err)
		return
	}
	defer resp.Body.Close()

	// 检查响应状态码
	if resp.StatusCode != http.StatusOK {
		log.Printf("请求失败，状态码: %d", resp.StatusCode)
		return
	}
	// Step 6: 清空ResultForWeakPassword
	scanTask.ResultForWeakPassword = sync.Map{}
}

func HandleWebVulnScanTaskRequest(c *gin.Context) {
	// 校验身份（例如 API Key）
	apiKey := c.GetHeader("X-API-Key")
	if apiKey != "a3VyYWw=" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "未授权的访问"})
		return
	}
	// 解析请求体
	var req WebVulnScanTaskRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "无效的请求体"})
		return
	}
	log.Printf("开始执行扫描任务: TaskID=%s", req.TaskID)
	log.Printf("开始执行扫描任务: TaskType=%s", req.TaskType)
	log.Printf("开始执行扫描任务: WebVulnScanTarget=%s", req.Target)
	go func() {
		urlList := strings.Split(req.Target, ",")
		scanTask.RunWebVulnScanTask(urlList, req.PocContent, req.TaskID)
		webSocket.LastProgress[req.TaskID] = 0
		webSocket.CloseTaskConnections(req.TaskID)
		SendWebVulnScanResultToSpringBoot(req.TaskID)

	}()
	// 立即返回响应
	c.JSON(http.StatusOK, gin.H{
		"status":  "accepted",
		"taskId":  req.TaskID,
		"message": "任务已接收，正在后台执行",
	})

}

func SendWebVulnScanResultToSpringBoot(taskId string) {
	response := WebVulnScanResultResponse{
		TaskId: taskId,
		Result: make(map[string][]scanTask.SingleTargetWebVulnScanInfo),
	}
	scanTask.ResultForWebVulnScanTask.Range(func(key, value any) bool {
		targetUrl, ok := key.(string)
		if !ok {
			return true // 跳过无效的key
		}
		pocFilePath, ok := value.(string)
		if !ok {
			return true // 跳过无效的key
		}
		scanInfo := scanTask.SingleTargetWebVulnScanInfo{
			PocFilePath: pocFilePath,
		}
		if _, exists := response.Result[targetUrl]; !exists {
			response.Result[targetUrl] = []scanTask.SingleTargetWebVulnScanInfo{}
		}
		response.Result[targetUrl] = append(response.Result[targetUrl], scanInfo)

		return true
	})
	// Step 3: 将结果数据序列化为 JSON 格式
	jsonData, err := json.Marshal(response)
	if err != nil {
		log.Printf("JSON 序列化失败: %v", err)
		return
	}
	// Step 4: 创建 HTTP POST 请求
	url := "http://127.0.0.1:8888/api/v1/task/webVulnScanResult"
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		log.Printf("创建 HTTP 请求失败: %v", err)
		return
	}
	req.Header.Set("Content-Type", "application/json")

	// Step 5: 发送请求并处理响应
	client := &http.Client{}
	resp, err := client.Do(req)
	log.Printf("任务%s扫描结果已发送到SpringBoot", taskId)
	if err != nil {
		log.Printf("发送 HTTP 请求失败: %v", err)
		return
	}
	defer resp.Body.Close()

	// 检查响应状态码
	if resp.StatusCode != http.StatusOK {
		log.Printf("请求失败，状态码: %d", resp.StatusCode)
		return
	}
	// Step 6: 清空ResultForWebVulnScanTask
	scanTask.ResultForWebVulnScanTask = sync.Map{}
}
