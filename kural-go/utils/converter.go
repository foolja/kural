package utils

import (
	"errors"
	"fmt"
	"github.com/malfunkt/iprange"
	"kural/scanTask"
	"net"
	"strconv"
	"strings"
)

func GetPortList(input string) ([]int, error) {
	ports := []int{}
	if input == "" {
		return ports, nil
	}
	parts := strings.Split(input, ",") // 处理多个端口输入
	for _, part := range parts {
		part = strings.TrimSpace(part)
		if strings.Contains(part, "-") {
			// 处理端口范围，例如 1-1000
			rangeParts := strings.Split(part, "-")
			if len(rangeParts) != 2 {
				return nil, errors.New("无效的端口范围")
			}

			start, err := strconv.Atoi(rangeParts[0])
			if err != nil || start < 1 || start > 65535 {
				return nil, errors.New("端口范围起始值无效")
			}

			end, err := strconv.Atoi(rangeParts[1])
			if err != nil || end < 1 || end > 65535 || end < start {
				return nil, errors.New("端口范围结束值无效")
			}
			if start > end {
				return nil, fmt.Errorf("端口范围无效：%d 大于 %d", start, end)
			}
			for i := start; i <= end; i++ {
				ports = append(ports, i)
			}
		} else {
			// 处理单个端口
			port, err := strconv.Atoi(part)
			if err != nil || port < 1 || port > 65535 {
				return nil, fmt.Errorf("端口号 %s 无效", part)
			}
			ports = append(ports, port)
		}
	}

	return ports, nil

}
func GetIPList(ipList string) ([]net.IP, error) {
	addressList, err := iprange.ParseList(ipList)
	if err != nil {
		fmt.Println("解析 IP 失败")
		return nil, err
	}
	list := addressList.Expand()
	return list, err
}
func GetPortByService(protocol string) int {
	switch protocol {
	case "ssh":
		return 22
	case "http":
		return 80
	case "https":
		return 443
	case "ftp":
		return 21
	case "rdp":
		return 3389
	case "mysql":
		return 3306
	case "redis":
		return 6379
	case "mssql":
		return 1433
	case "mongodb":
		return 27017
	default:
		return 0 // 未知协议
	}
}
func ConvertToTargets(ipPort []string, usernames []string) []scanTask.WeakPasswordTarget {
	// 预分配切片容量（优化性能）
	total := len(ipPort) * len(usernames)
	targets := make([]scanTask.WeakPasswordTarget, 0, total)
	for _, item := range ipPort {
		parts := strings.Split(item, ":")
		if len(parts) == 2 {
			ip := parts[0]
			service := parts[1]
			port := GetPortByService(strings.ToLower(service))
			for _, username := range usernames {
				targets = append(targets, scanTask.WeakPasswordTarget{
					IP:       ip,
					Port:     port,
					Service:  service,
					Username: username,
				})
			}
		}

	}

	return targets
}
func ConvertIPsToStrings(ips []net.IP) []string {
	var ipStrings []string
	for _, ip := range ips {
		ipStrings = append(ipStrings, ip.String())
	}
	return ipStrings
}
