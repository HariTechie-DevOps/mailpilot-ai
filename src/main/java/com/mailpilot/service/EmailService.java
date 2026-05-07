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

    /**
     * Logical Step: Convert LocalDateTime to String to match the updated 
     * Candidate model and database schema.
     */
    public void scheduleInterview(List<Candidate> candidates, LocalDateTime time) {
        for (Candidate c : candidates) {
            // Fix: Convert LocalDateTime to String
            c.setInterviewTime(time.toString()); 
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

        // 1. Check for Rejection
        if (lowerText.contains("withdraw") || lowerText.contains("not interested")) return "REJECT";

        // 2. NEW LOGIC: Check if they just mentioned a day (Implicit Reschedule)
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String day : days) {
            if (lowerText.contains(day)) return "RESCHEDULE";
        }

        // 3. Check for Keywords
        if (lowerText.contains("reschedule") || lowerText.contains("change")) return "RESCHEDULE";

        // 4. Check for Confirmation
        if (lowerText.contains("confirm") || lowerText.contains("yes")) return "CONFIRM";

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
            // --- NEW PROTECTION LOGIC START ---
            // If the candidate is already WITHDRAWN, we block any further automatic updates.
            if ("WITHDRAWN".equalsIgnoreCase(c.getStatus())) {
                System.out.println("LOGIC BLOCK: Skipping update for " + c.getName() + " because they have already withdrawn.");
                return; // This acts like 'continue' in a lambda, skipping to the next candidate.
            }
            // --- NEW PROTECTION LOGIC END ---

            if (intent.equals("CONFIRM")) {
                c.setStatus("CONFIRMED");
            } else if (intent.equals("RESCHEDULE")) {
                c.setStatus("RESCHEDULE_REQUESTED");
        
                String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
                for (String day : days) {
                    if (body.contains(day)) {
                        String formattedDay = day.substring(0, 1).toUpperCase() + day.substring(1);
                        c.setInterviewTime(formattedDay + " (Pending Approval)");
                        break; 
                    }
                }
            } else if (intent.equals("REJECT")) {
                c.setStatus("WITHDRAWN");
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


    public String detectIntentAI(String text) {

        String prompt =
                "Classify intent: CONFIRM, RESCHEDULE, UNKNOWN\nText: " + text;

        // OpenAI API logic later

        if (text.toLowerCase().contains("confirm")) {
            return "CONFIRM";
        }

        if (text.toLowerCase().contains("reschedule")) {
            return "RESCHEDULE";
        }

        return "UNKNOWN";
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
