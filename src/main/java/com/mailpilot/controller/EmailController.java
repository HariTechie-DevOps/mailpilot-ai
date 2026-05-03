package com.mailpilot.controller;

import com.mailpilot.model.IncomingEmail;
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
        List<Candidate> shortlisted = candidateRepository.findByStatus("SHORTLISTED");
        emailService.sendToShortlisted(shortlisted);
        return "Emails sent to " + shortlisted.size() + " shortlisted candidates.";
    }

    @PostMapping("/schedule")
    public String scheduleInterview(@RequestParam String time) {
        try {
            LocalDateTime interviewTime = LocalDateTime.parse(time);
            List<Candidate> shortlisted = candidateRepository.findByStatus("SHORTLISTED");
            
            if (shortlisted.isEmpty()) {
                return "No candidates found with status 'SHORTLISTED'.";
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

    @PostMapping("/incoming")
    public String receiveEmail(@RequestBody IncomingEmail email) {
        return emailService.processIncomingEmail(email);
    }

    @GetMapping("/report")
    public String getRecruitmentReport() {
        long total = candidateRepository.count();
        long confirmed = candidateRepository.countByStatus("CONFIRMED");
        long shortlisted = candidateRepository.countByStatus("SHORTLISTED");
        long rescheduled = candidateRepository.countByStatus("RESCHEDULE_REQUESTED");

        double successRate = (total > 0) ? ((double) confirmed / total) * 100 : 0;

        return String.format(
            "--- MailPilot Analytics Dashboard ---\n" +
            "Confirmed: %d\n" +
            "Shortlisted: %d\n" +
            "Reschedule Requested: %d\n" +
            "-------------------------------------\n" +
            "Success Rate: %.2f%%\n" +
            "Total Pipeline: %d",
            confirmed, shortlisted, rescheduled, successRate, total
        );
    }

    @PostMapping("/send-interview/{id}")
    public String sendInterviewEmail(@PathVariable Long id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);

        if (candidateOpt.isEmpty()) {
            return "Candidate not found";
        }

        Candidate candidate = candidateOpt.get();
        String subject = "Interview Invitation";
        String body = "Dear " + candidate.getName() + ",\n\nYou are invited for an interview.";

        return emailService.sendEmail(candidate.getEmail(), subject, body);
    }
}
