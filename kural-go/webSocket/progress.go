package webSocket

import (
	"github.com/gin-gonic/gin"
	"log"
	"strconv"
)

func ProgressExecutor(current, total int, taskId string) {
	progress := current * 100 / total
	log.Printf("任务" + taskId + "当前进度" + strconv.Itoa(progress))
	ProgressMu.Lock()
	lastSent := LastProgress[taskId]
	if progress > lastSent { // 只发送更大的进度值
		LastProgress[taskId] = progress // 更新记录
		ProgressMu.Unlock()

		BroadcastProgress(taskId, progress) // 执行广播
	} else {
		ProgressMu.Unlock()
	}
}
func BroadcastProgress(taskID string, progress int) {
	ClientsMu.Lock()
	defer ClientsMu.Unlock()
	for _, conn := range Clients[taskID] {

		if err := conn.WriteJSON(gin.H{"taskId": taskID, "progress": progress}); err != nil {
			log.Println("发送进度失败:", err)
			conn.Close()
		}
	}
}
