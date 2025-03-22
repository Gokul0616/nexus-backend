package com.nexus.nexus.MyPackage.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JwtRequestUtil jwtRequestUtil;

    @Autowired
    public WebSocketConfig(JwtRequestUtil jwtRequestUtil) {
        this.jwtRequestUtil = jwtRequestUtil;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtRequestUtil))
                .setAllowedOrigins("*");
    }
}
    