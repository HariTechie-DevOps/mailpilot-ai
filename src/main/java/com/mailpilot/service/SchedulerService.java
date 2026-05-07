package com.mailpilot.service;

import com.mailpilot.model.IncomingEmail;
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
        // Phase 4: Fetching actual email data
        List<IncomingEmail> emails = gmailReaderService.readEmails();

        for (IncomingEmail email : emails) {
            System.out.println("Processing email from: " + email.getFrom());
            
            // Logic: Pass the full object to the processor
            String result = emailService.processIncomingEmail(email);
            
            System.out.println("Result: " + result);
        }
    }
}
