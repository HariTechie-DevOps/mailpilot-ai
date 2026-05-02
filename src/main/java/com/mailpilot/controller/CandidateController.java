package com.mailpilot.controller;

import com.mailpilot.model.Candidate;
import com.mailpilot.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    // CREATE: Add a new candidate
    @PostMapping
    public Candidate addCandidate(@RequestBody Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    // READ: Get all candidates (This allows browser testing)
    @GetMapping
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    // DELETE: Remove a candidate by ID
    @DeleteMapping("/{id}")
    public String deleteCandidate(@PathVariable Long id) {
        candidateRepository.deleteById(id);
        return "Candidate with ID " + id + " deleted successfully.";
    }
}
