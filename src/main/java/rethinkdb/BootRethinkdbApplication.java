package rethinkdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BootRethinkdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootRethinkdbApplication.class, args);
    }
}
