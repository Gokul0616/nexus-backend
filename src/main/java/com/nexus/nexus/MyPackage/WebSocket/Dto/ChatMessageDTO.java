package com.nexus.nexus.MyPackage.WebSocket.Dto;

import lombok.Data;

@Data
public class ChatMessageDTO {

    private String type; // SUBSCRIBE, UNSUBSCRIBE, MESSAGE
    private String topic; // target topic (e.g. userId or roomId)
    private String from;
    private String content;
    private String roomId;
    private String timestamp;

    public ChatMessageDTO() {
    }

}
