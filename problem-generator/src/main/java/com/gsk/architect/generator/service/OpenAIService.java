package com.gsk.architect.generator.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OpenAIService {
    private final OpenAiService service;
    private static final String MODEL = "gpt-4";

    public OpenAIService(String apiKey) {
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public String generateResponse(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "You are a senior system design interviewer at FAANG companies."));
        messages.add(new ChatMessage("user", prompt));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .temperature(0.7)
                .build();

        try {
            return service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("Error generating response from OpenAI", e);
            throw new RuntimeException("Failed to generate response", e);
        }
    }
}
