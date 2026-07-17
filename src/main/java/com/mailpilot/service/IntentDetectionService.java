package com.mailpilot.service;

import org.springframework.stereotype.Service;

@Service
public class IntentDetectionService {

    public String detectIntent(String text) {

        if (text == null || text.isBlank()) {
            return "UNKNOWN";
        }

        String cleanText = text.toLowerCase();

        if (cleanText.matches(".*(withdraw|cancel|not interested|reject).*")) {
            return "REJECT";
        }

        if (cleanText.matches(".*(reschedule|change|another time|postpone).*")) {
            return "RESCHEDULE";
        }

        if (cleanText.matches(".*(confirm|yes|okay|ok|perfect|sure).*")) {
            return "CONFIRM";
        }

        return "UNKNOWN";
    }
}
