package rethinkdb.chat;

import com.rethinkdb.RethinkDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rethinkdb.db.RethinkDBConnectionFactory;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    public static final RethinkDB r = RethinkDB.r;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

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
