package rethinkdb.chat;

import com.rethinkdb.RethinkDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rethinkdb.db.RethinkDBConnectionFactory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    protected final Logger log = LoggerFactory.getLogger(ChatController.class);
    private static final RethinkDB r = RethinkDB.r;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    @RequestMapping(method = RequestMethod.POST)
    public ChatMessage postMessage(@RequestBody ChatMessage chatMessage) {
        chatMessage.setTime(OffsetDateTime.now());
        HashMap run = r.db("chat").table("messages").insert(chatMessage)
                .run(connectionFactory.createConnection());

        log.info("Insert {}", run);
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
