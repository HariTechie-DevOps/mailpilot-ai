package com.mailpilot.controller;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import com.mailpilot.service.CandidateWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

   @Autowired
    private CandidateWorkflowService candidateWorkflowService;

    @Autowired
    private CandidateRepository candidateRepository;

    // CREATE: Add a new candidate
    @PostMapping
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return candidateWorkflowService.createCandidate(candidate);
    }

    // READ: Get all candidates (This allows browser testing)
    @GetMapping
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @GetMapping("/{id}")
    public Candidate getCandidate(@PathVariable Long id) {
        return candidateRepository.findById(id).orElse(null);
    }
    
    // DELETE: Remove a candidate by ID
    @DeleteMapping("/{id}")
    public String deleteCandidate(@PathVariable Long id) {
        candidateRepository.deleteById(id);
        return "Candidate with ID " + id + " deleted successfully.";
    }
}
