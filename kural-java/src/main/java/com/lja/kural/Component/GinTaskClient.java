package com.lja.kural.Component;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.lja.kural.Service.BaseScanTaskService;
import com.lja.kural.Vo.PortScanTaskRequestVo;
import com.lja.kural.Vo.WeakPasswordScanTaskRequestVo;
import com.lja.kural.Vo.WebVulnScanTaskRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import java.net.URI;
@Slf4j
@Component
public class GinTaskClient {
    @Autowired
    private TaskWebSocketServerHandler taskWebSocketServerHandler;
    @Autowired
    private BaseScanTaskService baseScanTaskService;
    @Autowired
    private ScanTaskSender scanTaskSender;

    WebSocketHttpHeaders headers = new WebSocketHttpHeaders(); // 关键修正点：添加请求头
    // 同时发送任务到 Gin，并建立 WebSocket 连接监听进度
    public void startTaskAndListenProgress(String taskId, PortScanTaskRequestVo taskData) {

        //建立 WebSocket 连接监听 Gin 的进度
        connectToGinWebSocket(taskId);
        // 发送任务到 Gin 的 HTTP 接口
        scanTaskSender.sendTaskToGin(taskData);

    }

    public void startTaskAndListenProgress(String taskId, WeakPasswordScanTaskRequestVo taskData) {

        //建立 WebSocket 连接监听 Gin 的进度
        connectToGinWebSocket(taskId);
        // 发送任务到 Gin 的 HTTP 接口
        scanTaskSender.sendTaskToGin(taskData);

    }
    public void startTaskAndListenProgress(String taskId, WebVulnScanTaskRequestVo taskData) {

        //建立 WebSocket 连接监听 Gin 的进度
        connectToGinWebSocket(taskId);
        // 发送任务到 Gin 的 HTTP 接口
        scanTaskSender.sendTaskToGin(taskData);

    }

    private void connectToGinWebSocket(String taskId) {
        WebSocketClient client = new StandardWebSocketClient();
        String wsUrl = "ws://localhost:9999/ws/" + taskId;

        client.doHandshake(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("✅ Connected to Gin WebSocket for taskId: {}" , taskId);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                String payload = message.getPayload().toString();
                log.info("任务进度: {}",payload);
                // 收到 Gin 的进度后，直接转发给前端
                taskWebSocketServerHandler.broadcastProgress(taskId, payload);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                log.error("🚨 Gin WebSocket error");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.warn("❌ Gin WebSocket closed for taskId: {}",taskId);
                try {
                    Integer taskProgress = taskWebSocketServerHandler.getTaskProgress(taskId);
                    if(taskProgress == 100){
                        baseScanTaskService.updateTaskStatus(taskId,"已完成");
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        },headers,URI.create(wsUrl));
    }
}
