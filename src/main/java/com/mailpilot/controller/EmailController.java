package com.mailpilot.controller;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import com.mailpilot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping("/send-shortlisted")
    public String sendToShortlisted() {

        List<Candidate> shortlisted =
                candidateRepository.findByStatus("SHORTLISTED");

        emailService.scheduleInterview(shortlisted, LocalDateTime.now());

        return "Emails sent to " + shortlisted.size() + " shortlisted candidates.";
    }

    @PostMapping("/schedule")
    public String scheduleInterview(@RequestParam String time) {
        // Logical Check: Ensure the string has a 'T' (e.g., 2026-05-15T10:30:00)
        try {
            LocalDateTime interviewTime = LocalDateTime.parse(time);
            List<Candidate> shortlisted = candidateRepository.findByStatus("SHORTLISTED");
        
            if (shortlisted.isEmpty()) {
                return "No candidates found with status 'SHORTLISTED'. Please update your database.";
            }

            emailService.scheduleInterview(shortlisted, interviewTime);
            return "Interview scheduled for " + shortlisted.size() + " candidates.";
        } catch (Exception e) {
            return "Error: Invalid time format. Please use YYYY-MM-DDTHH:MM:SS";
        }
    }

    @PostMapping("/send-to-all")
    public String sendToAll() {
        List<Candidate> all = candidateRepository.findAll();
        emailService.sendBulkEmail(all);
        return "Process started for " + all.size() + " candidates.";
    }

    @PostMapping("/send-interview/{id}")
    public String sendInterviewEmail(@PathVariable Long id) {

        Optional<Candidate> candidateOpt = candidateRepository.findById(id);

        if (candidateOpt.isEmpty()) {
            return "Candidate not found";
        }

        Candidate candidate = candidateOpt.get();

        String subject = "Interview Invitation";
        String body = "Dear " + candidate.getName() +
                ",\n\nYou are invited for an interview.\n\nRegards,\nHR";

        return emailService.sendEmail(candidate.getEmail(), subject, body);
    }
}
