package scanTask

import (
	"bytes"
	"fmt"
	"io"
	"kural/config"
	"kural/webSocket"
	"log"
	"net/http"
	"os"
	"regexp"
	"strings"
	"sync"
	"sync/atomic"
	"text/template"
	"time"
)

type WebVulnScanTaskTarget struct {
	Url         string
	PocFilePath string
}
type SingleTargetWebVulnScanInfo struct {
	PocFilePath string `json:"pocFilePath"`
}

var (
	ResultForWebVulnScanTask sync.Map
)

func RunWebVulnScanTask(urlList []string, userPocContent string, taskId string) {
	filesPath, _ := CollectYAMLFiles("poc-yaml")
	taskCh := GenerateWebVulnScanTasks(urlList, userPocContent, filesPath)
	total := len(urlList) * len(filesPath)
	var current int32
	// 启动worker池
	var wg sync.WaitGroup

	for i := 0; i < config.ThreadNum; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			workerForWebVulnScanTask(taskCh, &current, total, taskId)
		}()
	}

	// 结果收集
	wg.Wait()
}
func GenerateWebVulnScanTasks(urlList []string, userPocContent string, filesPath []string) chan WebVulnScanTaskTarget {
	taskCh := make(chan WebVulnScanTaskTarget, config.ThreadNum)
	go func() {
		defer close(taskCh)
		err := os.WriteFile("poc-yaml/userDefine.yaml", []byte(userPocContent), 0666)
		if err != nil {
			panic(err)
		}
		for _, url := range urlList {

			for _, filePath := range filesPath {
				taskCh <- WebVulnScanTaskTarget{Url: url, PocFilePath: filePath}
			}
		}
	}()
	return taskCh
}

func workerForWebVulnScanTask(taskCh chan WebVulnScanTaskTarget, current *int32, total int, taskId string) {
	for task := range taskCh {
		atomic.AddInt32(current, 1)
		webSocket.ProgressExecutor(int(atomic.LoadInt32(current)), total, taskId)
		success := ExecuteSinglePocFile(task.PocFilePath, task.Url)
		if success {
			log.Printf("%s发现漏洞", task.Url)
			ResultForWebVulnScanTask.Store(task.Url, task.PocFilePath)
		}
	}
}

func ExecuteSinglePocFile(filePath string, targetUrl string) bool {
	resultsMap := make(map[string]bool)
	poc, err := LoadPOC(filePath)
	if err != nil {
		panic(err)
	}
	if len(poc.Requests) == 0 {
		return false
	}
	for reqName, req := range poc.Requests {
		response, err := SendRequest(targetUrl, req, poc.Variables)
		if err != nil {
			return false
		}
		result := EvaluateRule(req.Rule, response, false, nil)
		reqNameFunc := reqName + "()"
		resultsMap[reqNameFunc] = result

	}
	expression := poc.Expression
	return EvaluateRule(expression, nil, true, resultsMap)

}

// 发送 HTTP 请求
func SendRequest(targetURL string, req HTTPRequest, vars map[string]string) (*POCResponse, error) {
	startTime := time.Now()
	client := &http.Client{
		Timeout: 10 * time.Second,
		CheckRedirect: func(req *http.Request, via []*http.Request) error {
			return http.ErrUseLastResponse // 禁止自动重定向
		},
	}

	// 替换变量
	path := replaceVariables(req.Path, vars)
	body := replaceVariables(req.Body, vars)

	// 构造请求
	httpReq, err := http.NewRequest(req.Method, targetURL+path, bytes.NewBufferString(body))
	if err != nil {
		return nil, fmt.Errorf("构造请求失败: %v", err)
	}

	// 设置请求头
	for key, value := range req.Headers {
		httpReq.Header.Set(key, replaceVariables(value, vars))
	}

	// 发送请求
	resp, err := client.Do(httpReq)
	if err != nil {
		return nil, fmt.Errorf("请求发送失败: %v", err)
	}

	// 计算耗时
	duration := time.Since(startTime)
	// 封装响应
	return wrapPOCResponse(resp, duration)
}

// 将 HTTP 响应封装到 POCResponse 结构体
func wrapPOCResponse(resp *http.Response, duration time.Duration) (*POCResponse, error) {
	if resp == nil {
		return nil, fmt.Errorf("nil HTTP response")
	}

	// 读取响应体
	defer resp.Body.Close()
	bodyBytes, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("读取响应体失败: %v", err)
	}

	// 转换 Headers 为 map[string]string
	headers := make(map[string]string)
	for key, values := range resp.Header {
		// 合并多值 Header（例如：Cookie）
		headers[key] = strings.Join(values, "; ")
	}

	// 获取 Content-Type（去除可能的 charset）
	contentType := "unknown"
	if ct := resp.Header.Get("Content-Type"); ct != "" {
		contentType = strings.Split(ct, ";")[0]
	}

	return &POCResponse{
		Status:      resp.StatusCode,
		Body:        bodyBytes,
		BodyString:  string(bodyBytes),
		Headers:     headers,
		ContentType: contentType,
		DelayTime:   int(duration.Milliseconds()), // 延迟时间（毫秒）
	}, nil
}

// 替换变量（如 {{username}}）
func replaceVariables(input string, vars map[string]string) string {

	// 预处理：将 {{variable}} 转为 {{.variable}}
	re := regexp.MustCompile(`{{\s*([a-zA-Z0-9_]+)\s*}}`)
	processedInput := re.ReplaceAllString(input, "{{.$1}}")
	tmpl, err := template.New("vars").Parse(processedInput)
	if err != nil {
		return input
	}

	var buf bytes.Buffer
	if err := tmpl.Execute(&buf, vars); err != nil {
		return input
	}
	return buf.String()
}
