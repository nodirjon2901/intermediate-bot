package uz.result.intermediatebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IntermediateBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntermediateBotApplication.class, args);
    }

}

