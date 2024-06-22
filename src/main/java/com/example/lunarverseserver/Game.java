package com.example.lunarverseserver;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Game {
    private WebSocketSession[] players = {null, null};
    int activePlayer = 0;

    boolean acceptingInput = false;

    public Game() {
        
    }

    public boolean connectSession(WebSocketSession session) {
        for (int i = 0; i < players.length; ++i) {
            if (!isConnected(players[i])) {
                players[i] = session;
                if (acceptingInput && isActivePlayer(session)) {
                    sendAcceptInputMessage();
                }
                return true;
            }
        }
        return false;
    }

    public boolean isConnected(WebSocketSession session) {
        return session != null && session.isOpen();
    }

    public void setActivePlayer(int number) {
        activePlayer = number;
    }

    public WebSocketSession getActivePlayer() {
        if (isConnected(players[activePlayer])) {
            return players[activePlayer];
        }
        return null;
    }

    public boolean isActivePlayer(WebSocketSession session) {
        return getActivePlayer() != null && session.getId().equals(getActivePlayer().getId());
    }

    public void acceptInput() {
        acceptingInput = true;
        sendAcceptInputMessage();
    }

    private void sendAcceptInputMessage() {
        send(getActivePlayer(), new JSONObject()
            .put("type", "acceptInput")
        );
    }

    public void print(Object value) {
        sendAll(new JSONObject()
            .put("type", "print")
            .put("message", value.toString())
        );
    }

    public void println(Object value) {
        this.print(value.toString() + "\r\n");
    }

    public void sendAll(JSONObject message) {
        for (WebSocketSession peer : players) {
            if (isConnected(peer)) {
                send(peer, message);
            }
        }
    }

    public void send(WebSocketSession session, JSONObject message) {
        if (session == null) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(message.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
