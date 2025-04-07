package com.lja.kural.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.lja.kural.Utils.SystemInfoUtil;
@Slf4j
@Component
public class SystemInfoWebSocketServerHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("获取系统信息的websocket获取连接");
        // 创建单线程调度器（每个连接独立）
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // 启动定时任务（每秒发送一次）
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    Double cpuUsageRate = SystemInfoUtil.getCpuUsageRate();
                    Double memoryUsageRate = SystemInfoUtil.getMemoryUsageRate();
                    // 或者手动拼接（无依赖方案）
                    String manualJson = String.format("{\"cpuUsageRate\":%d,\"memoryUsageRate\":%d}", (int) Math.round(cpuUsageRate), (int) Math.round(memoryUsageRate));
                    session.sendMessage(new TextMessage(manualJson));
                }
            } catch (Exception e) {
                // 异常时主动关闭连接
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (Exception ex) {
                    // 关闭异常处理
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        // 将调度器和任务绑定到会话
        session.getAttributes().put("executor", executor);
        session.getAttributes().put("future", future);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        cleanupResources(session);
        super.afterConnectionClosed(session, status);
        log.warn("获取系统信息的websocket连接断开");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        cleanupResources(session);
        super.handleTransportError(session, exception);
    }

    private void cleanupResources(WebSocketSession session) {
        // 取消定时任务
        ScheduledFuture<?> future = (ScheduledFuture<?>) session.getAttributes().get("future");
        if (future != null) {
            future.cancel(true);
        }

        // 关闭执行器
        ScheduledExecutorService executor = (ScheduledExecutorService) session.getAttributes().get("executor");
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
