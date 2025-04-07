package utils

import (
	"fmt"
	"net"
	"strings"
	"time"
)

func CheckAlive(ipList []net.IP, serviceTypeList []string) []string {
	aliveTarget := make([]string, 0, len(ipList)*len(serviceTypeList))
	for _, ip := range ipList {
		for _, service := range serviceTypeList {
			port := GetPortByService(strings.ToLower(service))
			conn, err := net.DialTimeout("tcp", fmt.Sprintf("%v:%v", ip, port), 2*time.Second)
			defer func() {
				if conn != nil {
					_ = conn.Close()
				}
			}()
			if err == nil {
				aliveTarget = append(aliveTarget, fmt.Sprintf("%v:%v", ip, service))
			}
		}
	}
	return aliveTarget
}
