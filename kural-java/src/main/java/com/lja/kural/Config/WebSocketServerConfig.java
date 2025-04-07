package com.lja.kural.Config;

import com.lja.kural.Component.SystemInfoWebSocketServerHandler;
import com.lja.kural.Component.TaskWebSocketServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket  // 启用 WebSocket
public class WebSocketServerConfig implements WebSocketConfigurer {
    @Autowired
    private TaskWebSocketServerHandler taskWebSocketServerHandler;
    @Autowired
    private SystemInfoWebSocketServerHandler systemInfoWebSocketServerHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 配置 WebSocket 端点，映射到 "/task" 路径
        registry.addHandler(taskWebSocketServerHandler, "/task-progress/{taskId}")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        // 从路径中提取taskId
                        String path = request.getURI().getPath();
                        String taskId = path.substring(path.lastIndexOf('/') + 1);
                        attributes.put("taskId", taskId);
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                    }
                })
                .setAllowedOrigins("*");
        // 第二个端点
        registry.addHandler(systemInfoWebSocketServerHandler, "/system-Info")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        return true;
                    }
                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                    }
                })
                .setAllowedOrigins("*");
    }
}
