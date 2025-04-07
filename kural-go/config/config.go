package config

import (
	"time"
)

type ConnectFuncPoint func(ip string, port int) (string, int, error)

var (
	ThreadNum     = 500
	TimeOutForTCP = 2
	// 超时时间
	TimeOutForSSH                  = 2 * time.Second
	ConnectProxy  ConnectFuncPoint = nil
)
