package com.gsk.architect.generator;

import com.gsk.architect.generator.model.Domain;
import com.gsk.architect.generator.service.*;

public class ProblemGeneratorApp {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java ProblemGeneratorApp <MODEL_TYPE> <CONFIG> <DOMAIN>");
            System.out.println("MODEL_TYPE: OPENAI or LLAMA");
            System.out.println("CONFIG: OpenAI API key or path to Llama model file");
            System.out.println("Available domains: ECOMMERCE, VIDEO_STREAMING, APPLE, GENERIC");
            return;
        }

        String modelType = args[0].toUpperCase();
        String config = args[1];
        String domainStr = args[2].toUpperCase();

        try {
            Domain domain = Domain.valueOf(domainStr);

            // Create the appropriate language model service
            LanguageModelService modelService;
            try {
                ModelFactory.ModelType type = ModelFactory.ModelType.valueOf(modelType);
                modelService = ModelFactory.createModel(type, config);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid model type. Available types: OPENAI, LLAMA");
                return;
            }

            FileGeneratorService fileGeneratorService = new FileGeneratorService();
            QuestionGeneratorService questionGenerator = new QuestionGeneratorService(modelService, fileGeneratorService);

            questionGenerator.generateQuestion(domain);
            System.out.println("Successfully generated question for domain: " + domain);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid domain. Available domains: ECOMMERCE, VIDEO_STREAMING, APPLE, GENERIC");
        } catch (Exception e) {
            System.out.println("Error generating question: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
