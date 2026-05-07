package com.mailpilot.service;

import com.mailpilot.model.Candidate;
import com.mailpilot.model.EmailLog;
import com.mailpilot.model.IncomingEmail;
import com.mailpilot.repository.CandidateRepository;
import com.mailpilot.repository.EmailLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    // =========================================================
    // SEND NORMAL EMAIL
    // =========================================================

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

    // =========================================================
    // BULK EMAIL
    // =========================================================

    public void sendBulkEmail(List<Candidate> candidates) {

        for (Candidate c : candidates) {

            sendEmail(
                    c.getEmail(),
                    "Interview Update",
                    "Hello " + c.getName()
            );
        }
    }

    // =========================================================
    // SEND TO SHORTLISTED
    // =========================================================

    public void sendToShortlisted(List<Candidate> candidates) {

        for (Candidate c : candidates) {

            sendEmail(
                    c.getEmail(),
                    "Shortlisted",
                    "Dear " + c.getName() +
                            ", you are shortlisted for the interview."
            );
        }
    }

    // =========================================================
    // SCHEDULE INTERVIEW
    // =========================================================

    public void scheduleInterview(List<Candidate> candidates,
                                  LocalDateTime time) {

        for (Candidate c : candidates) {

            c.setInterviewTime(time);

            c.setStatus("INTERVIEW_SCHEDULED");

            candidateRepository.save(c);

            String subject = "Interview Scheduled";

            String body =
                    "Dear " + c.getName() +
                            ",\n\nYour interview is scheduled at: "
                            + time +
                            "\n\nRegards,\nHR Team";

            sendEmail(c.getEmail(), subject, body);
        }
    }

    // =========================================================
    // AI INTENT DETECTION
    // =========================================================

    public String detectIntent(String text) {
        if (text == null) return "UNKNOWN";
        String cleanText = text.toLowerCase();

        if (cleanText.matches(".*(withdraw|cancel|not interested).*")) return "REJECT";
        if (cleanText.matches(".*(reschedule|change|another time).*")) return "RESCHEDULE";
        if (cleanText.matches(".*(confirm|yes|okay|perfect).*")) return "CONFIRM";
        
        return "UNKNOWN";
    }

    // =========================================================
    // AUTO SCHEDULE
    // =========================================================

    public LocalDateTime autoSchedule() {

        return LocalDateTime.now()
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    // =========================================================
    // PROCESS INCOMING EMAIL
    // =========================================================

   public String processIncomingEmail(IncomingEmail email) {

    String intent = detectIntent(email.getBody());

    Optional<Candidate> optionalCandidate =
            candidateRepository.findByEmailIgnoreCase(email.getFrom());

    // =========================================================
    // VALIDATION
    // =========================================================

    if (optionalCandidate.isEmpty()) {
        return "Candidate not found";
    }

    if (intent.equals("UNKNOWN")) {
        return "Unknown intent";
    }

    Candidate candidate = optionalCandidate.get();

    // =========================================================
    // PROTECTION LOGIC
    // =========================================================

    if ("WITHDRAWN".equalsIgnoreCase(candidate.getStatus())) {

        System.out.println(
                "Skipping withdrawn candidate: "
                        + candidate.getName()
        );

        return "Candidate already withdrawn";
    }

    // =========================================================
    // CONFIRM
    // =========================================================

    if (intent.equals("CONFIRM")) {

        candidate.setStatus("CONFIRMED");

        candidateRepository.save(candidate);

        sendEmail(
                candidate.getEmail(),
                "Interview Confirmed",
                "Thank you for confirming your interview."
        );

        return "Confirmation processed";
    }

    // =========================================================
    // RESCHEDULE
    // =========================================================

    else if (intent.equals("RESCHEDULE")) {

        LocalDateTime newTime = autoSchedule();

        candidate.setInterviewTime(newTime);

        candidate.setStatus("RESCHEDULED");

        candidateRepository.save(candidate);

        sendEmail(
                candidate.getEmail(),
                "New Interview Schedule",
                "Your new interview time is: "
                        + newTime
        );

        return "Reschedule processed";
    }

    // =========================================================
    // REJECT
    // =========================================================

    else if (intent.equals("REJECT")) {

        candidate.setStatus("WITHDRAWN");

        candidateRepository.save(candidate);

        sendEmail(
                candidate.getEmail(),
                "Application Withdrawn",
                "Your application has been marked as withdrawn."
        );

        return "Withdrawal processed";
    }

    return "Processing completed";
}

    // =========================================================
    // SAVE EMAIL LOG
    // =========================================================

    private void saveLog(String to,
                         String subject,
                         String body,
                         String status) {

        EmailLog log = new EmailLog();

        log.setRecipient(to);

        log.setSubject(subject);

        log.setBody(body);

        log.setStatus(status);

        log.setTimestamp(LocalDateTime.now());

        emailLogRepository.save(log);
    }
}
