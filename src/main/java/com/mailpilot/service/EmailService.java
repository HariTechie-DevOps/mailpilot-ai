package com.mailpilot.service;

import com.mailpilot.model.EmailLog;
import com.mailpilot.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.mailpilot.model.Candidate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private CandidateRepository candidateRepository; 

    public void sendBulkEmail(List<Candidate> candidates) {
        for (Candidate c : candidates) {
            sendEmail(c.getEmail(), "Interview Invite", "Hello " + c.getName());
        }
    }

    public void sendToShortlisted(List<Candidate> candidates) {

        for (Candidate c : candidates) {

            String subject = "Interview Invitation";
            String body = "Dear " + c.getName() +
                    ",\n\nYou are shortlisted for interview.\n\nRegards,\nHR";

            sendEmail(c.getEmail(), subject, body);
        }
    }

    public void scheduleInterview(List<Candidate> candidates, LocalDateTime time) {

        for (Candidate c : candidates) {

            c.setInterviewTime(time);
            c.setStatus("INTERVIEW_SCHEDULED");

            candidateRepository.save(c);

            String subject = "Interview Scheduled";
            String body = "Dear " + c.getName() +
                    ",\n\nYour interview is scheduled at: " + time +
                    "\n\nRegards,\nHR";

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
