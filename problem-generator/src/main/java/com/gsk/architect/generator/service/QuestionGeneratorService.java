package com.gsk.architect.generator.service;

import com.gsk.architect.generator.model.Domain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionGeneratorService {
    private final OpenAIService openAIService;
    private final FileGeneratorService fileGeneratorService;

    public QuestionGeneratorService(OpenAIService openAIService, FileGeneratorService fileGeneratorService) {
        this.openAIService = openAIService;
        this.fileGeneratorService = fileGeneratorService;
    }

    private static final String QUESTION_PROMPT_TEMPLATE = """
            Generate a detailed system design interview question for %s domain.
            The question should:
            1. Be challenging and similar to real FAANG interview questions
            2. Require usage of various Data Structures and Algorithms in the solution
            3. Include specific requirements and constraints
            4. Be focused on scalability and distributed systems
            5. Include expected throughput, latency requirements, and data volume estimates
            
            Format the response in Markdown format.
            """;

    private static final String HINTS_PROMPT_TEMPLATE = """
            For the following system design question, generate step-by-step hints that will guide the candidate
            without giving away the complete solution:
            
            %s
            
            Format the response in Markdown format with clear sections and bullet points.
            """;

    public void generateQuestion(Domain domain) {
        log.info("Generating question for domain: {}", domain);

        // Generate the main question
        String questionPrompt = String.format(QUESTION_PROMPT_TEMPLATE, domain.getDescription());
        String questionContent = openAIService.generateResponse(questionPrompt);

        // Generate hints
        String hintsPrompt = String.format(HINTS_PROMPT_TEMPLATE, questionContent);
        String hintsContent = openAIService.generateResponse(hintsPrompt);

        // Generate sanitized package name from the first line of the question
        String packageName = generatePackageName(questionContent);

        // Create the files in lab-solutions project
        fileGeneratorService.generateQuestionFiles(domain, packageName, questionContent, hintsContent);
    }

    private String generatePackageName(String questionContent) {
        String firstLine = questionContent.split("\\r?\\n")[0]
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "_");
        return firstLine.length() > 50 ? firstLine.substring(0, 50) : firstLine;
    }
}
