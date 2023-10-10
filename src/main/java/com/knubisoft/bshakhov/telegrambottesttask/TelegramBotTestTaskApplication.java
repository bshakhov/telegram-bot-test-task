package com.knubisoft.bshakhov.telegrambottesttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotTestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotTestTaskApplication.class, args);
    }

}
