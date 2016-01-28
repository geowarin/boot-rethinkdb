package rethinkdb.db;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.ConnectionInstance;

import java.util.concurrent.TimeoutException;

public class RethinkDBConnectionFactory {
    private String host;

    public RethinkDBConnectionFactory(String host) {
        this.host = host;
    }

    public Connection<ConnectionInstance> createConnection() {
        try {
            return RethinkDB.r.connection().hostname(host).connect();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
