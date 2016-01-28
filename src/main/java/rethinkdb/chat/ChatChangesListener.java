package rethinkdb.chat;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rethinkdb.db.RethinkDBConnectionFactory;

import java.util.HashMap;

@Service
public class ChatChangesListener {
    protected final Log log = LogFactory.getLog(getClass());

    public static final RethinkDB r = RethinkDB.r;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Async
    public void pushChangesToWebSocket() {
        Cursor<HashMap> cursor = r.db("chat").table("messages").changes()
                .getField("new_val")
                .without("time")
                .run(connectionFactory.createConnection());

        while (cursor.hasNext()) {
            HashMap data = cursor.next();
            ChatMessage chatMessage = new ChatMessage((String) data.get("message"), (String) data.get("from"));
            log.info("New message: " + chatMessage.message);
            webSocket.convertAndSend("/topic/messages", chatMessage);
        }
    }
}
