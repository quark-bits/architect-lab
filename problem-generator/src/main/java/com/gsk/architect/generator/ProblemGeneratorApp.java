package com.gsk.architect.generator;

import com.gsk.architect.generator.model.Domain;
import com.gsk.architect.generator.service.FileGeneratorService;
import com.gsk.architect.generator.service.OpenAIService;
import com.gsk.architect.generator.service.QuestionGeneratorService;

public class ProblemGeneratorApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ProblemGeneratorApp <OPENAI_API_KEY> <DOMAIN>");
            System.out.println("Available domains: ECOMMERCE, VIDEO_STREAMING, APPLE, GENERIC");
            return;
        }

        String apiKey = args[0];
        String domainStr = args[1].toUpperCase();

        try {
            Domain domain = Domain.valueOf(domainStr);
            OpenAIService openAIService = new OpenAIService(apiKey);
            FileGeneratorService fileGeneratorService = new FileGeneratorService();
            QuestionGeneratorService questionGenerator = new QuestionGeneratorService(openAIService, fileGeneratorService);

            questionGenerator.generateQuestion(domain);
            System.out.println("Successfully generated question for domain: " + domain);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid domain. Available domains: ECOMMERCE, VIDEO_STREAMING, APPLE, GENERIC");
        } catch (Exception e) {
            System.out.println("Error generating question: " + e.getMessage());
        }
    }
}
