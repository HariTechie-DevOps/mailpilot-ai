package com.mailpilot.controller;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return candidateRepository.save(candidate);
    }
}
