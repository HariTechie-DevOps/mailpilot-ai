package com.mailpilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.mailpilot")
@EnableScheduling
public class MailpilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailpilotApplication.class, args);
    }
}
