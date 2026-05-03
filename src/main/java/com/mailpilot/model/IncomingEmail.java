package com.mailpilot.model;

public class IncomingEmail {

    private String from;
    private String subject;
    private String body;

    // Getters & Setters

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
