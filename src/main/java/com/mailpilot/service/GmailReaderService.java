package com.mailpilot.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.mailpilot.model.IncomingEmail;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GmailReaderService {

    private static final String APPLICATION_NAME = "MailPilot AI";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Logic: Authenticates the user and returns a Gmail service instance.
     */
    private Gmail getGmailService() throws Exception {
        InputStream in = GmailReaderService.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(in));

        // Logic: Define what permissions we need (Reading and Modifying for marking as read)
        List<String> scopes = Collections.singletonList(GmailScopes.GMAIL_MODIFY);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // Logic: This opens the browser for the "Handshake"
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), 
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<IncomingEmail> readEmails() {
        List<IncomingEmail> emails = new ArrayList<>();
        try {
            Gmail service = getGmailService();

            // Logic: Only fetch "UNREAD" messages
            ListMessagesResponse response = service.users().messages().list("me")
                    .setQ("is:unread").execute();

            List<Message> messages = response.getMessages();

            if (messages != null) {
                for (Message message : messages) {
                    // Fetch full message details
                    Message fullMessage = service.users().messages().get("me", message.getId()).execute();
                    
                    IncomingEmail incoming = new IncomingEmail();
                    
                    // Logic: Extract Snippet as the body
                    incoming.setBody(fullMessage.getSnippet());
                    
                    // Logic: Extract "From" header
                    fullMessage.getPayload().getHeaders().forEach(header -> {
                        if (header.getName().equals("From")) {
                            // Extract just the email address from "Name <email@xxx.com>"
                            String rawFrom = header.getValue();
                            if (rawFrom.contains("<")) {
                                incoming.setFrom(rawFrom.substring(rawFrom.indexOf("<") + 1, rawFrom.indexOf(">")));
                            } else {
                                incoming.setFrom(rawFrom);
                            }
                        }
                    });

                    emails.add(incoming);

                    // Logic: Mark message as read (remove UNREAD label) so we don't process it twice
                    service.users().messages().batchModify("me", 
                        new com.google.api.services.gmail.model.BatchModifyMessagesRequest()
                            .setIds(Collections.singletonList(message.getId()))
                            .setRemoveLabelIds(Collections.singletonList("UNREAD")))
                        .execute();
                }
            }
        } catch (Exception e) {
            System.err.println("Problem-Solving Error: Failed to fetch Gmail data - " + e.getMessage());
        }
        return emails;
    }
}
