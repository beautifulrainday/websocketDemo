package com.websocket.demo.config;

import com.websocket.demo.handler.MyHandler;
import com.websocket.demo.handler.RoomHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyHandler(), "myHandler/{ID}").setAllowedOrigins("*");
        registry.addHandler(new RoomHandler(), "roomHandler/{roomNo}/{userId}").setAllowedOrigins("*");
    }

}