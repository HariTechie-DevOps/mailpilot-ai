package com.mailpilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mailpilot")
public class MailpilotApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailpilotApplication.class, args);
    }
}
