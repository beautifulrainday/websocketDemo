package com.websocket.demo.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketInterceptor implements HandshakeInterceptor {

    //在握手之前执行该方法, 继续握手返回true, 中断握手返回false. 通过attributes参数设置WebSocketSession的属性
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            String userId = request.getURI().toString().split("userId=")[1];
            String roomNo = request.getURI().toString().split("roomNo=")[1].split("/")[0];
            System.out.println("当前session的ID=" + userId + "当前房间号：" + roomNo);
        }
        return true;
    }

    //完成握手之后执行该方法
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        System.out.println("进来webSocket的afterHandshake拦截器！");
    }
}