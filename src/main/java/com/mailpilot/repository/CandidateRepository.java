package com.mailpilot.repository;

import com.mailpilot.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    List<Candidate> findByStatus(String status);

    // Logic: Find a single candidate by email to process their reply intent
    Optional<Candidate> findByEmailIgnoreCase(String email);
}
