package com.mailpilot.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.mailpilot.model.Candidate;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OfferGeneratorService {

    @Autowired
    private JavaMailSender mailSender;

    public void generateAndSendOffer(Candidate candidate) {
        String filePath = "Offer_" + candidate.getName().replaceAll("\\s+", "_") + ".pdf";

        try {
            // 1. Create the PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("OFFER LETTER").setBold().setFontSize(20));
            document.add(new Paragraph("Dear " + candidate.getName() + ","));
            document.add(new Paragraph("We are pleased to offer you the position of " + candidate.getRole() + " at our company."));
            document.add(new Paragraph("We look forward to having you on the team."));
            document.add(new Paragraph("Regards, HR Team"));
            
            document.close();

            // 2. Send the Email with the PDF attached
            sendEmailWithAttachment(candidate.getEmail(), filePath);

            System.out.println("Offer generated and sent to: " + candidate.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmailWithAttachment(String toEmail, String filePath) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Your Official Offer Letter");
        helper.setText("Dear Candidate, please find your offer letter attached.");

        FileSystemResource file = new FileSystemResource(new File(filePath));
        helper.addAttachment("OfferLetter.pdf", file);

        mailSender.send(message);
    }
}
