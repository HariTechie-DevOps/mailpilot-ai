package com.mailpilot.template;

import com.mailpilot.model.Candidate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailTemplateService {

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

    // =====================================================
    // SHORTLIST EMAIL
    // =====================================================

    public String getShortlistSubject() {
        return "Congratulations! You have been shortlisted";
    }

    public String getShortlistBody(Candidate candidate) {

        return String.format("""
                Dear %s,

                Congratulations!

                We are pleased to inform you that you have been shortlisted for the next stage of our recruitment process.

                Our recruitment team will schedule your interview shortly.

                Thank you for your interest in joining our organization.

                Best Regards,

                HR Team
                MailPilot Recruitment
                """,
                candidate.getName());
    }

    // =====================================================
    // INTERVIEW EMAIL
    // =====================================================

    public String getInterviewSubject() {
        return "Interview Invitation";
    }

    public String getInterviewBody(Candidate candidate) {

        return String.format("""
                Dear %s,

                Congratulations!

                Your interview has been scheduled.

                Interview Details

                Date & Time:
                %s

                Mode:
                Online

                Please reply with one of the following:

                YES
                OR
                Request Reschedule

                Looking forward to speaking with you.

                Regards,

                HR Team
                MailPilot Recruitment
                """,
                candidate.getName(),
                candidate.getInterviewTime().format(formatter));
    }

    // =====================================================
    // CONFIRM EMAIL
    // =====================================================

    public String getConfirmSubject() {
        return "Interview Confirmation";
    }

    public String getConfirmBody(Candidate candidate) {

        return String.format("""
                Dear %s,

                Thank you for confirming your interview.

                We look forward to meeting you on

                %s

                Kindly join the meeting 5 minutes early.

                Best Regards,

                HR Team
                MailPilot Recruitment
                """,
                candidate.getName(),
                candidate.getInterviewTime().format(formatter));
    }

    // =====================================================
    // RESCHEDULE EMAIL
    // =====================================================

    public String getRescheduleSubject() {
        return "Interview Rescheduled";
    }

    public String getRescheduleBody(Candidate candidate) {

        return String.format("""
                Dear %s,

                As requested, your interview has been successfully rescheduled.

                New Interview Time

                %s

                We look forward to speaking with you.

                Regards,

                HR Team
                MailPilot Recruitment
                """,
                candidate.getName(),
                candidate.getInterviewTime().format(formatter));
    }

    // =====================================================
    // WITHDRAW EMAIL
    // =====================================================

    public String getWithdrawSubject() {
        return "Application Withdrawal Confirmation";
    }

    public String getWithdrawBody(Candidate candidate) {

        return String.format("""
                Dear %s,

                We have successfully processed your withdrawal request.

                Thank you for showing interest in our organization.

                We wish you all the very best in your future career.

                Regards,

                HR Team
                MailPilot Recruitment
                """,
                candidate.getName());
    }

}
