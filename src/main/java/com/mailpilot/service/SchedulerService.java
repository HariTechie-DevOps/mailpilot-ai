package com.mailpilot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private GmailReaderService gmailReaderService;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000)
    public void checkEmails() {

        List<String> emails = gmailReaderService.readEmails();

        for (String body : emails) {

            System.out.println("New Email: " + body);

            // Process logic later
        }
    }
}
