package com.mailpilot.service;

import com.mailpilot.model.IncomingEmail;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class GmailReaderService {

    public List<IncomingEmail> readEmails() {
        List<IncomingEmail> emails = new ArrayList<>();

        // Real-world example simulation:
        // In a full production env, this would use the Gmail API to fetch 'FROM' and 'BODY'
        IncomingEmail email1 = new IncomingEmail();
        email1.setFrom("john.doe@example.com");
        email1.setBody("I confirm the interview for tomorrow");
        emails.add(email1);

        return emails;
    }
}
