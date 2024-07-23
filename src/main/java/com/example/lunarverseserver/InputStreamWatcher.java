package com.example.lunarverseserver;

import java.io.IOException;
import java.io.InputStream;

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
                        game.handleGameOutput(output);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
