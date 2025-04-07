package scanTask

import (
	"fmt"
	"gopkg.in/yaml.v3"
	"os"
	"path/filepath"
	"strings"
)

// POC模板结构体
type POC struct {
	ID         string                 `yaml:"id"`
	Info       TemplateInfo           `yaml:"info"`
	Variables  map[string]string      `yaml:"variables"`
	Requests   map[string]HTTPRequest `yaml:"requests"`
	Expression string                 `yaml:"expression"`
}

type TemplateInfo struct {
	Name        string `yaml:"name"`
	Author      string `yaml:"author"`
	Severity    string `yaml:"severity"`
	Description string `yaml:"description"`
}

type HTTPRequest struct {
	Method  string            `yaml:"method"`
	Path    string            `yaml:"path"`
	Headers map[string]string `yaml:"headers"`
	Body    string            `yaml:"body"`
	Rule    string            `yaml:"rule"`
}
type POCResponse struct {
	Status      int
	Body        []byte
	BodyString  string
	Headers     map[string]string
	ContentType string
	DelayTime   int
}

// 全局函数映射表

var PocUtilFuncMap map[string]func(*POCResponse, interface{}) bool

func (pocResponse *POCResponse) contains(arg interface{}) bool {
	// 检查参数是否为字符串类型
	content, ok := arg.(string)
	if !ok {
		return false // 如果参数不是字符串类型，返回 false
	}

	// 检查 BodyString 是否包含 content
	return strings.Contains(pocResponse.BodyString, content)
}
func (pocResponse *POCResponse) icontains(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) startsWith(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) endsWith(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) bcontains(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) ibcontains(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) bstartsWith(arg interface{}) bool {
	return true
}
func (pocResponse *POCResponse) matches(arg interface{}) bool {
	return true
}
func LoadPOC(path string) (*POC, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("读取文件失败: %v", err)
	}

	var poc POC
	if err := yaml.Unmarshal(data, &poc); err != nil {
		return nil, fmt.Errorf("解析 YAML 失败: %v", err)
	}

	return &poc, nil
}
func CollectYAMLFiles(dir string) ([]string, error) {
	var files []string

	err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err // 处理访问文件时可能出现的错误
		}
		// 如果是普通文件且扩展名为.yaml，则添加到结果
		if !info.IsDir() && filepath.Ext(path) == ".yaml" {
			files = append(files, path)
		}
		return nil
	})

	if err != nil {
		return nil, err
	}
	return files, nil
}

func init() {
	// 初始化 funcMap
	PocUtilFuncMap = make(map[string]func(*POCResponse, interface{}) bool)

	// 注册方法
	PocUtilFuncMap["contains"] = (*POCResponse).contains
	PocUtilFuncMap["icontains"] = (*POCResponse).icontains
	PocUtilFuncMap["startsWith"] = (*POCResponse).startsWith
	PocUtilFuncMap["endsWith"] = (*POCResponse).endsWith
	PocUtilFuncMap["bcontains"] = (*POCResponse).bcontains
	PocUtilFuncMap["ibcontains"] = (*POCResponse).ibcontains
	PocUtilFuncMap["bstartsWith"] = (*POCResponse).bstartsWith
	PocUtilFuncMap["matches"] = (*POCResponse).matches
}
