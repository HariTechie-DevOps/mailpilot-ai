package com.mailpilot.controller;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import com.mailpilot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidateRepository candidateRepository;

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
