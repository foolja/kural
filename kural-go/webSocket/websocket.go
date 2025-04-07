package webSocket

import (
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"sync"
)

var (
	Upgrader = websocket.Upgrader{
		//ReadBufferSize: 设置读取缓冲区的大小。默认值是4096字节，设置为2048字节可以减少内存使用，但可能会增加读取次数。
		ReadBufferSize: 2048,
		//WriteBufferSize: 设置写入缓冲区的大小。默认值是4096字节，设置为2048字节可以减少内存使用，但可能会增加写入次数。
		WriteBufferSize: 2048,
		//CheckOrigin: 一个函数，用于检查WebSocket连接的来源。这里设置为总是返回true，允许任何来源的连接。如果你需要更严格的跨域控制，可以在这里添加逻辑。
		CheckOrigin: func(r *http.Request) bool { return true },
	}
	//clients: 一个全局的映射，用于存储每个任务（taskID）对应的WebSocket连接列表。一个任务可能有多个连接（例如，同一个任务的多个客户端连接）。
	Clients = make(map[string][]*websocket.Conn)
	//clientsMu: 一个互斥锁，用于保护clients映射的并发访问。在Go中，map不是线程安全的，因此在多个协程中访问map时需要使用锁来避免数据竞争。
	ClientsMu sync.Mutex // 保护clients的互斥锁
	// LastProgress 每个任务最后一次发送的进度
	LastProgress = make(map[string]int)
	// 保护 map 的互斥锁
	ProgressMu sync.Mutex
)

func HandleWebSocket(c *gin.Context) {
	taskID := c.Param("taskId")

	// 检查请求是否是WebSocket请求
	if c.Request.Header.Get("Upgrade") != "websocket" {
		c.Status(http.StatusBadRequest)
		return
	}
	//使用 upgrader.Upgrade 方法将HTTP连接升级为WebSocket连接。如果升级失败，记录错误并返回。
	conn, err := Upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("WebSocket upgrade failed:", err)
		return
	}
	defer conn.Close()

	//使用 clientsMu 互斥锁保护 clients 映射的并发访问。
	//将新的WebSocket连接 conn 添加到 clients 映射中对应 taskID 的连接列表中。这里假设 clients[taskID] 是一个连接列表。
	//如果 clients[taskID] 不存在，则自动创建一个新的空列表并添加 conn。
	ClientsMu.Lock()
	Clients[taskID] = append(Clients[taskID], conn)
	ClientsMu.Unlock()

	//使用一个无限循环 for 来保持WebSocket连接活跃。NextReader 会阻塞等待下一个消息的到来。
	//如果读取消息时发生错误（例如，连接断开），则记录错误并关闭连接。
	//使用 clientsMu 互斥锁保护 clients 映射的并发访问。
	//从 clients[taskID] 中移除已经断开的连接 conn。
	for {
		_, _, err := conn.NextReader()
		if err != nil {
			// 连接断开时移除
			ClientsMu.Lock()
			list, ok := Clients[taskID]
			if ok {
				for i, client := range list {
					if client == conn {
						list = append(list[:i], list[i+1:]...)
						break
					}
				}
				if len(list) == 0 {
					delete(Clients, taskID)
				} else {
					Clients[taskID] = list
				}
			}
			ClientsMu.Unlock()
			break
		}
	}
}

// 👇 新增函数：关闭指定任务的所有 WebSocket 连接
func CloseTaskConnections(taskID string) {
	ClientsMu.Lock()
	defer ClientsMu.Unlock()

	// 遍历该任务的所有连接
	for _, conn := range Clients[taskID] {
		// 发送关闭帧并关闭连接
		_ = conn.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.CloseNormalClosure, "scanTask completed"))
		conn.Close()
	}

	// 从 clients 中移除该任务
	delete(Clients, taskID)
}
