package com.mailpilot.service;

import com.mailpilot.model.Candidate;
import com.mailpilot.model.EmailLog;
import com.mailpilot.repository.CandidateRepository;
import com.mailpilot.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    public void scheduleInterview(List<Candidate> candidates, LocalDateTime time) {
        for (Candidate c : candidates) {
            c.setInterviewTime(time);
            c.setStatus("INTERVIEW_SCHEDULED");
            candidateRepository.save(c);

            String subject = "Interview Scheduled";
            String body = "Dear " + c.getName() + ",\n\nYour interview is at: " + time;
            sendEmail(c.getEmail(), subject, body);
        }
    }

    public String sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
            saveLog(to, subject, body, "SENT");
            return "Email Sent Successfully";
        } catch (Exception e) {
            saveLog(to, subject, body, "FAILED");
            return "Email Failed: " + e.getMessage();
        }
    }

    public String detectIntent(String text) {

        text = text.toLowerCase();

        if (text.contains("reschedule")) {
            return "RESCHEDULE";
        } else if (text.contains("confirm") || text.contains("yes")) {
            return "CONFIRM";
        } else {
            return "UNKNOWN";
        }
    }
    
    public void sendToShortlisted(List<Candidate> candidates) {
        for (Candidate c : candidates) {
            String subject = "Congratulations! You are Shortlisted";
            String body = "Dear " + c.getName() + ",\n\nWe are pleased to inform you that you have been shortlisted for the next round.";
            sendEmail(c.getEmail(), subject, body);
        }
    }

    private void saveLog(String to, String subject, String body, String status) {
        EmailLog log = new EmailLog();
        log.setRecipient(to);
        log.setSubject(subject);
        log.setBody(body);
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        emailLogRepository.save(log);
    }
    
    public void sendBulkEmail(List<Candidate> candidates) {
        for (Candidate c : candidates) sendEmail(c.getEmail(), "Update", "Hello " + c.getName());
    }
}
