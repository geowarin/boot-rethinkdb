package rethinkdb.model;

import java.util.Date;

public class ChatMessage {
    public String message;
    public String from;
    public Date time;

    public ChatMessage() {
    }

    public ChatMessage(String message, String from) {
        this.message = message;
        this.from = from;
    }
}
