package com.nexus.nexus.MyPackage.WebSocket;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.util.UriComponentsBuilder;

import com.nexus.nexus.MyPackage.Configuration.JwtRequestUtil;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtRequestUtil jwtRequestUtil;

    public JwtHandshakeInterceptor(JwtRequestUtil jwtRequestUtil) {
        this.jwtRequestUtil = jwtRequestUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        String token = null;
        HttpHeaders headers = request.getHeaders();
        List<String> authHeaders = headers.get("Authorization");

        if (authHeaders != null && !authHeaders.isEmpty()) {
            token = authHeaders.get(0).replace("Bearer ", "");
        } else {
            URI uri = request.getURI();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
            token = queryParams.getFirst("token");
        }

        if (token == null || !jwtRequestUtil.validateToken(token)) {
            System.err.println("JWT validation failed. Aborting handshake.");
            return false;
        }

        String userId = jwtRequestUtil.extractUsername(token);

        attributes.put("jwt", token);
        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {

    }
}
