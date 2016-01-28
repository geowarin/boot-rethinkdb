package rethinkdb.controller;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Cursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rethinkdb.db.RethinkDBConnectionFactory;
import rethinkdb.model.ChatMessage;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class RethinkController {

    public static final RethinkDB r = RethinkDB.r;
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        Cursor<HashMap> cur = r.db("chat").table("messages").changes()
                .getField("new_val")
                .without("time")
                .run(connectionFactory.createConnection());

        new Thread(() -> {
            while (cur.hasNext()) {
                HashMap data = cur.next();
                ChatMessage chatMessage = new ChatMessage((String) data.get("message"), (String) data.get("from"));
                log.info("New message: " + chatMessage.message);
                webSocket.convertAndSend("/topic/messages", chatMessage);
            }
        }).start();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ChatMessage postMessage(@RequestBody ChatMessage chatMessage) {
        r.db("chat").table("messages").insert(
                r.hashMap()
                        .with("message", chatMessage.message)
                        .with("from", chatMessage.from)
                        .with("time", r.now())
        ).run(connectionFactory.createConnection());

        return chatMessage;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChatMessage> getMessages() {

        List<ChatMessage> messages = r.db("chat").table("messages")
                .orderBy().optArg("index", r.desc("time"))
                .limit(20)
                .orderBy("time")
                .run(connectionFactory.createConnection(), ChatMessage.class);

        return messages;
    }
}
