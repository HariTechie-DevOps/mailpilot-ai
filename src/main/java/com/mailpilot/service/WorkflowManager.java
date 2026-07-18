package com.mailpilot.service;

import org.springframework.stereotype.Service;

public enum CandidateStatus {
    APPLIED, SHORTLISTED, INTERVIEW_SCHEDULED, CONFIRMED, OFFER_SENT, WITHDRAWN
}

@Service
public class WorkflowManager {
    public CandidateStatus nextStatus(CandidateStatus current, String intent) {
        // Logic: Map current state + intent to new state
        if (current == CandidateStatus.SHORTLISTED && "CONFIRM".equals(intent)) {
            return CandidateStatus.INTERVIEW_SCHEDULED;
        }
        return current; // Return current if no transition found
    }
}
