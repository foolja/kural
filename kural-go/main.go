package main

import (
	"github.com/gin-gonic/gin"
	"kural/web"
	"kural/webSocket"
	"runtime"
)

func main() {
	router := gin.Default()
	router.GET("/ws/:taskId", webSocket.HandleWebSocket)
	router.POST("/api/v2/task/portScanTask", web.HandlePortScanRequest)
	router.POST("/api/v2/task/weakPasswordTask", web.HandleWeakPasswordRequest)
	router.POST("/api/v2/task/webVulnScanTask", web.HandleWebVulnScanTaskRequest)
	router.Run("127.0.0.1:9999")
}

func init() {
	//让 Go 程序并行地使用系统中所有的 CPU 核心。
	runtime.GOMAXPROCS(runtime.NumCPU())
}
