package com.example.lunarverseserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Game {
    final static String appPath = "/home/sockyman/src/LVTest/bin/";
    final static String appMainClass = "LunarVerse/GameSim";

    Process process;
    BufferedInputStream stdout;
    PrintStream stdin;

    private WebSocketSession[] players = {null, null};
    int activePlayer = 0;

    boolean acceptingInput = false;

    public Game() {
        
    }

    private void startGame() {
        ProcessBuilder builder = new ProcessBuilder("java", appMainClass, "online");
        builder.directory(new File(appPath));
        builder.redirectError(Redirect.INHERIT);
        //builder.redirectInput(Redirect.INHERIT);
        try {
            this.process = builder.start();
            this.stdout = new BufferedInputStream(this.process.getInputStream());
            this.stdin = new PrintStream(new BufferedOutputStream(this.process.getOutputStream()));

            InputStreamWatcher isw = new InputStreamWatcher(this.stdout, this);
            isw.start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connectSession(WebSocketSession session) {
        this.sendAll(new JSONObject().put("type", "print").put("message", "New player connecting\r\n"));
        for (int i = 0; i < players.length; ++i) {
            if (!isConnected(players[i])) {
                players[i] = session;
                if (acceptingInput && isActivePlayer(session)) {
                    sendAcceptInputMessage();
                }

                if (i == 1) {
                    startGame();
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
        //System.out.println(message.toString());
        for (WebSocketSession peer : players) {
            if (isConnected(peer)) {
                send(peer, message);
            }
        }
    }

    public void sendOthers(WebSocketSession session, JSONObject message) {
        //System.out.println(message.toString());
        for (WebSocketSession peer : players) {
            if (isConnected(peer) && session != peer) {
                send(peer, message);
            }
        }
    }

    public void handleInput(WebSocketSession session, String input) {
        if (this.stdin == null) {
            return;
        }
        this.sendOthers(session, new JSONObject().put("type", "print").put("message", input));
        this.stdin.print(input);
        this.stdin.flush();
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

    public boolean isRunning() {
        return true;
    }
}
