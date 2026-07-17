package com.mailpilot.service;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateWorkflowService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmailService emailService;

    public Candidate createCandidate(Candidate candidate) {

        Candidate savedCandidate = candidateRepository.save(candidate);

        if ("SHORTLISTED".equalsIgnoreCase(savedCandidate.getStatus())) {

            emailService.sendToShortlisted(List.of(savedCandidate));

        }

        return savedCandidate;
    }
}
