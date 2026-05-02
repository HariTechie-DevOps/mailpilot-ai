package com.mailpilot.service;

import com.mailpilot.model.EmailLog;
import com.mailpilot.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    public String sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);

            EmailLog log = new EmailLog();
            log.setRecipient(to);
            log.setSubject(subject);
            log.setBody(body);
            log.setStatus("SENT");
            log.setTimestamp(LocalDateTime.now());

            emailLogRepository.save(log);

            return "Email Sent Successfully";

        } catch (Exception e) {

            EmailLog log = new EmailLog();
            log.setRecipient(to);
            log.setSubject(subject);
            log.setBody(body);
            log.setStatus("FAILED");
            log.setTimestamp(LocalDateTime.now());

            emailLogRepository.save(log);

            return "Email Failed: " + e.getMessage();
        }
    }
}
