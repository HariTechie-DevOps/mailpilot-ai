package com.mailpilot.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class GmailReaderService {

    public List<String> readEmails() {

        // Connect Gmail API
        // Fetch unread emails
        // Return email bodies

        List<String> emails = new ArrayList<>();

        emails.add("Yes I confirm my interview");
        emails.add("Can we reschedule?");

        return emails;
    }
}
