package com.gsk.architect.generator.service;

import com.gsk.architect.generator.model.Domain;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileGeneratorService {
    private static final String LAB_SOLUTIONS_PATH = "../lab-solutions/src/main/java/com/gsk/architect/solutions";

    public void generateQuestionFiles(Domain domain, String questionPackage, String questionContent, String hintsContent) {
        try {
            // Create the package directory structure
            String packagePath = String.format("%s/%s/%s",
                    LAB_SOLUTIONS_PATH,
                    domain.name().toLowerCase(),
                    questionPackage);

            Path dirPath = Paths.get(packagePath);
            Files.createDirectories(dirPath);

            // Create README.md with the question
            Path readmePath = dirPath.resolve("README.md");
            Files.writeString(readmePath, questionContent);

            // Create HINTS.md with the hints
            Path hintsPath = dirPath.resolve("HINTS.md");
            Files.writeString(hintsPath, hintsContent);

            // Create an empty Solution.java file
            String solutionTemplate = generateSolutionTemplate(domain, questionPackage);
            Path solutionPath = dirPath.resolve("Solution.java");
            Files.writeString(solutionPath, solutionTemplate);

            log.info("Successfully generated question files in: {}", packagePath);
        } catch (IOException e) {
            log.error("Error generating question files", e);
            throw new RuntimeException("Failed to generate question files", e);
        }
    }

    private String generateSolutionTemplate(Domain domain, String questionPackage) {
        return String.format("""
                package com.gsk.architect.solutions.%s.%s;
                
                /**
                 * Solution template for the system design problem.
                 * Implement your solution here.
                 */
                public class Solution {
                    // TODO: Implement your solution
                }
                """, domain.name().toLowerCase(), questionPackage);
    }
}
