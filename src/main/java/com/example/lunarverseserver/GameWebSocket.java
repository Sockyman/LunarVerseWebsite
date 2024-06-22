package com.example.lunarverseserver;

import java.io.IOException;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import org.json.JSONObject;

public class GameWebSocket extends TextWebSocketHandler {
    Game game;

    public GameWebSocket() {
        this.game = new Game();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("Opened " + session.getId());
        if (!game.connectSession(session)) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Closed " + session.getId() + " " + status);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, InterruptedException {
        System.out.println(message.getPayload() + " " + session.getId());
        
        JSONObject json = new JSONObject(message.getPayload());

        switch (json.getString("type")) {
            case "input":
                synchronized (game) {
                    game.handleInput(session, json.getString("message"));
                }
                break;
            default:
                break;
        }
 
    }
}
