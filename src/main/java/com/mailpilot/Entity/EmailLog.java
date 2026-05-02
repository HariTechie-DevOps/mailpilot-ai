package com.mailpilot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    private String subject;

    @Column(length = 2000)
    private String body;

    private String status;
    private LocalDateTime timestamp;

    // Getters & Setters
}
