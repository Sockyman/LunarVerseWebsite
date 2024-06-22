package com.example.lunarverseserver;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

public class InputStreamWatcher extends Thread {
    InputStream stream;
    Game game;

    public InputStreamWatcher(InputStream stream, Game game) {
        this.stream = stream;
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (stream.available() != 0) {
                    String output = new String(stream.readNBytes(stream.available()));
                    synchronized (game) {
                        game.sendAll(new JSONObject()
                            .put("type", "print")
                            .put("message", output)
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
