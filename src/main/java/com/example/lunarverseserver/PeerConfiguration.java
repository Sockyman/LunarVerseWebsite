package com.example.lunarverseserver;

import org.json.JSONObject;

public class PeerConfiguration {
    public enum PeerType {
        Player,
        Spectator,
        Referee;

        public String toString() {
            switch (this) {
                case Player:
                    return "player";
                case Spectator:
                    return "spectator";
                case Referee:
                    return "referee";
            }
            throw new RuntimeException("Invalid peer type");
        }
    }

    public PeerType type;

    boolean acceptingInput;

    public PeerConfiguration(PeerType type) {
        this.type = type;
        this.acceptingInput = false;
    }

    public boolean isAcceptingInput() {
        switch (this.type) {
            case Player:
                return this.acceptingInput;
            case Spectator:
                return false;
            case Referee:
                return true;
        }
        return false;
    }

    public JSONObject getJSON() {
        return new JSONObject()
            .append("type", "config")
            .append("peerType", this.type.toString())
            .append("acceptInput", this.isAcceptingInput());
    }
}
