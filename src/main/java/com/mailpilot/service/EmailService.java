package com.mailpilot.service;

import com.mailpilot.model.IncomingEmail;
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
            c.setInterviewTime(time); // Fixed: Passing LocalDateTime directly
            c.setStatus("INTERVIEW_SCHEDULED");
            candidateRepository.save(c);

            String subject = "Interview Scheduled";
            String body = "Dear " + c.getName() + ",\n\nYour interview is scheduled for: " + time;
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
        if (text == null) return "UNKNOWN";
        String lowerText = text.toLowerCase();

        // Logic: New Rejection Keywords
        boolean isRejection = lowerText.contains("not interested") || 
                             lowerText.contains("withdraw") || 
                             lowerText.contains("another offer") || 
                             lowerText.contains("decline");

        if (isRejection) return "REJECT";

        boolean isNegative = lowerText.contains("not") || lowerText.contains("can't") || 
                            lowerText.contains("cannot") || lowerText.contains("unable");

        if (lowerText.contains("reschedule") || lowerText.contains("change")) {
            return "RESCHEDULE";
        } 
    
        if ((lowerText.contains("confirm") || lowerText.contains("yes")) && !isNegative) {
            return "CONFIRM";
        }

        return "UNKNOWN";
    }

    public String processIncomingEmail(IncomingEmail email) {
        String intent = detectIntent(email.getBody());
        String body = email.getBody().toLowerCase();

        List<Candidate> matchingCandidates = candidateRepository.findAll().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email.getFrom()))
                .toList();

        if (matchingCandidates.isEmpty()) return "Candidate not found";
        if (intent.equals("UNKNOWN")) return "Unknown intent";

        matchingCandidates.forEach(c -> {
            if (intent.equals("CONFIRM")) {
                c.setStatus("CONFIRMED");
            } else if (intent.equals("RESCHEDULE")) {
                c.setStatus("RESCHEDULE_REQUESTED");
                
                // Logic: Since the DB field is LocalDateTime, we set a placeholder 
                // date far in the future or just leave it as is. 
                // For now, we will just update the status to avoid the Type Error.
            }
            candidateRepository.save(c);
        });

        return intent + " processed for " + matchingCandidates.size() + " record(s)";
    }

    public void sendToShortlisted(List<Candidate> candidates) {
        for (Candidate c : candidates) {
            sendEmail(c.getEmail(), "Shortlisted", "Dear " + c.getName() + ", you are shortlisted.");
        }
    }

    public void sendBulkEmail(List<Candidate> candidates) {
        for (Candidate c : candidates) {
            sendEmail(c.getEmail(), "Update", "Hello " + c.getName());
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
}
