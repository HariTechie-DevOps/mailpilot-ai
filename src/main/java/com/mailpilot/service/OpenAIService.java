package com.mailpilot.service;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OpenAIService {
    // Inject your key via environment variable: System.getenv("OPENAI_API_KEY")
    private final OpenAiService service = new OpenAiService("YOUR_OPENAI_API_KEY");

    public String detectIntent(String body) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(List.of(
                new ChatMessage("system", "Classify intent as: CONFIRM, RESCHEDULE, REJECT, or UNKNOWN. Only return the word."),
                new ChatMessage("user", body)
            ))
            .build();
        
        return service.createChatCompletion(request).getChoices().get(0).getMessage().getContent().trim();
    }
}
