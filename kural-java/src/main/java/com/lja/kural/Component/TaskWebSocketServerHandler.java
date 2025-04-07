package com.lja.kural.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lja.kural.Service.BaseScanTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
@Slf4j
@Component
public class TaskWebSocketServerHandler extends TextWebSocketHandler {
    @Autowired
    private BaseScanTaskService baseScanTaskService;

    // 存储每个 taskId 对应的前端会话
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> taskSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> taskProgressMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String taskId = (String) session.getAttributes().get("taskId");
        if(!taskProgressMap.containsKey(taskId))
        {
            Integer progress = baseScanTaskService.selectTaskProgress(taskId);
            taskProgressMap.put(taskId,"{\"taskId\":\""+taskId+"\",\"progress\":"+progress+"}");
        }
        log.info("taskId: {}连接成功",taskId);

        // 添加会话到任务会话列表
        taskSessions.computeIfAbsent(taskId, k -> new CopyOnWriteArrayList<>()).add(session);

        // 立即发送以前的进度给当前连接的前端
        sendInitialProgress(taskId,session);
    }
    //缺少连接断开时的操作
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String taskId = (String) session.getAttributes().get("taskId");
        if (taskId != null) {
            CopyOnWriteArrayList<WebSocketSession> sessions = taskSessions.get(taskId);
            if (sessions != null) {
                sessions.remove(session);
                log.warn("taskId: {}连接断开",taskId);
                // 如果该任务的会话列表为空，直接移除整个任务键
                if (sessions.isEmpty()) {
                    taskSessions.remove(taskId);
                }
            }
        }
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输错误:{} " , exception.getMessage());
    }
    // 发送初始进度 0
    private void sendInitialProgress(String taskId,WebSocketSession session) {
        try {
             // 可以采用 JSON 或其他格式
            session.sendMessage(new TextMessage(taskProgressMap.get(taskId)));
        } catch (Exception e) {
            log.error("转发任务{}初始进度到前端失败", taskId);
        }
    }

    // 将消息推送到对应 taskId 的所有前端会话
    public void broadcastProgress(String taskId, String message) {
        taskProgressMap.replace(taskId,message);
        CopyOnWriteArrayList<WebSocketSession> sessions = taskSessions.get(taskId);
        log.info("转发任务进度到前端{}",sessions);
        if (sessions != null) {
            sessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    log.error("转发任务{}进度到前端失败", taskId);
                }
            });
        }
    }
    @PreDestroy
    public void saveTaskProgress(){
        for (Map.Entry<String, String> entry : taskProgressMap.entrySet()) {
            String taskId = entry.getKey();
            try {
                Integer progress = getTaskProgress(taskId);
                baseScanTaskService.updateTaskProgress(taskId,progress);
                if(progress < 100 && progress > 0 ) {
                    baseScanTaskService.updateTaskStatus(taskId,"已取消");
                }else if(progress == 100){
                    baseScanTaskService.updateTaskStatus(taskId,"已完成");
                }
                log.info("保存任务{}进度与状态",taskId);
            } catch (JsonProcessingException e) {
                log.error("保存任务{}进度错误",taskId);
            }
        }
    }
    public Integer getTaskProgress(String taskId) throws JsonProcessingException {
        String taskProgress = taskProgressMap.get(taskId);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Integer> map = objectMapper.readValue(taskProgress, Map.class);
        return map.get("progress");
    }
}
