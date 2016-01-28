package rethinkdb.chat;

import java.time.OffsetDateTime;

public class ChatMessage {
    public String message;
    public String from;
    public OffsetDateTime time;

    public ChatMessage() {
    }

    public ChatMessage(String message, String from, OffsetDateTime time) {
        this.message = message;
        this.from = from;
        this.time = time;
    }
}
