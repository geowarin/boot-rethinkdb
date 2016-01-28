package rethinkdb.controller;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.ConnectionInstance;
import com.rethinkdb.net.Cursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rethinkdb.model.ChatMessage;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/chat")
public class RethinkController {

    public static final RethinkDB r = RethinkDB.r;
    public static final String DBHOST = "192.168.99.100";
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private SimpMessagingTemplate webSocket;

    @PostConstruct
    public void init() {
//        Connection<ConnectionInstance> connection = createRethinkConnection();
//        r.dbCreate("chat").run(connection);
//        r.db("chat").tableCreate("messages").run(connection);
//        r.db("chat").table("messages").indexCreate("time").run(connection);

        Cursor<ChatMessage> cur = r.db("chat").table("messages").changes()
                .getField("new_val")
                .without("time")
                .run(createRethinkConnection(), ChatMessage.class);

        new Thread(() -> {
            while (cur.hasNext()) {
                ChatMessage chatMessage = cur.next();
                log.info("New message: " + chatMessage.message);
                webSocket.convertAndSend("/topic/messages", chatMessage);
            }
        }).start();
    }

    private Connection<ConnectionInstance> createRethinkConnection() {
        try {
            return r.connection().hostname(DBHOST).connect();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ChatMessage postMessage(@RequestBody ChatMessage chatMessage) {
        r.db("chat").table("messages").insert(
                r.hashMap("message", chatMessage.message)
                        .with("from", chatMessage.from)
                        .with("time", r.now()))
                .run(createRethinkConnection());

        return chatMessage;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChatMessage> getMessages() {

        List<ChatMessage> messages = r.db("chat").table("messages")
                .orderBy().optArg("index", r.desc("time"))
                .limit(20)
                .orderBy("time")
                .run(createRethinkConnection(), ChatMessage.class);

        return messages;
    }
}
