package com.gsk.architect.generator;

import com.gsk.architect.generator.model.Domain;
import com.gsk.architect.generator.service.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProblemGeneratorApp {
    private static final long MIN_HEAP_SIZE = 4L * 1024 * 1024 * 1024; // 4GB minimum for 1.7B model
    private static final String USAGE_MESSAGE = """
        Usage: java -jar problem-generator.jar <MODEL_TYPE> <CONFIG> <DOMAIN>
        MODEL_TYPE: OPENAI or LLAMA
        CONFIG: OpenAI API key or path to Llama model file
        DOMAIN: ECOMMERCE, VIDEO_STREAMING, APPLE, GENERIC

        For Llama models, recommended memory settings:
          - 1.7B model: -Xmx4g
          - 7B model: -Xmx8g
          - 13B model: -Xmx16g
          - 30B+ models: -Xmx32g

        Example:
          OpenAI: java -jar problem-generator.jar OPENAI sk-your-api-key ECOMMERCE
          Llama:  java -Xmx4g -jar problem-generator.jar LLAMA /path/to/model.gguf GENERIC
        """;

    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.out.println(USAGE_MESSAGE);
                System.exit(1);
            }

            // Check memory settings before proceeding
            checkMemorySettings();

            String modelType = args[0].toUpperCase();
            String config = args[1];
            String domainStr = args[2].toUpperCase();

            // Validate domain
            Domain domain;
            try {
                domain = Domain.valueOf(domainStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Invalid domain. Available domains: " +
                    String.join(", ", Arrays.toString(Domain.values())));
                System.exit(1);
                return;
            }

            // Validate configuration and create model
            validateConfig(modelType, config);
            LanguageModelService modelService = ModelFactory.createModel(
                ModelFactory.ModelType.valueOf(modelType),
                config
            );

            // Create services
            FileGeneratorService fileGenerator = new FileGeneratorService();
            QuestionGeneratorService questionGenerator = new QuestionGeneratorService(modelService, fileGenerator);

            // Generate the question
            questionGenerator.generateQuestion(domain);
            System.out.println("Question generated successfully");

        } catch (IllegalArgumentException e) {
            log.error("Configuration error: {}", e.getMessage());
            System.err.println("Error: " + e.getMessage());
            System.out.println("\n" + USAGE_MESSAGE);
            System.exit(1);
        } catch (Exception e) {
            log.error("Fatal error: ", e);
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void checkMemorySettings() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long maxMemory = heapUsage.getMax();

        if (maxMemory < MIN_HEAP_SIZE) {
            System.err.println("\nWarning: Insufficient JVM heap space allocated!");
            System.err.println("Current maximum heap size: " + (maxMemory / (1024*1024)) + "MB");
            System.err.println("Recommended minimum: 4GB (-Xmx4g) for 1.7B model");
            System.err.println("For larger models, use:");
            System.err.println("  - 7B models: -Xmx8g");
            System.err.println("  - 13B models: -Xmx16g");
            System.err.println("  - 30B+ models: -Xmx32g\n");
            System.err.println("Example: java -Xmx4g -jar problem-generator.jar ...\n");
            throw new IllegalStateException("Insufficient memory allocated. Please increase JVM heap size.");
        }
    }

    private static void validateConfig(String modelType, String config) {
        if (modelType.equals("LLAMA")) {
            File modelFile = new File(config);
            if (!modelFile.exists()) {
                throw new IllegalArgumentException("Model file not found: " + config);
            }
            if (!modelFile.canRead()) {
                throw new IllegalArgumentException("Cannot read model file: " + config);
            }

            // Check if the model file is in GGUF format
            if (!config.toLowerCase().endsWith(".gguf")) {
                System.err.println("Warning: Model file does not have .gguf extension. " +
                    "Make sure it's a valid GGUF format model.");
            }

            // Validate model size and available memory
            long modelSize = modelFile.length();
            long freeMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();

            // For 1.7B model, we need about 4GB of heap space
            if (modelSize > freeMemory) {
                throw new IllegalArgumentException(
                    String.format("Insufficient memory for model (%.2fGB). Available: %.2fGB. " +
                                "Use -Xmx4g for 1.7B model.",
                                modelSize / (1024.0 * 1024 * 1024),
                                freeMemory / (1024.0 * 1024 * 1024))
                );
            }

            // Check if llama.cpp library exists
            Path libPath = Path.of(System.getProperty("user.home"), "llama-models/.llama/lib/libllama.dylib");
            if (!libPath.toFile().exists()) {
                throw new IllegalArgumentException(
                    "Llama.cpp library not found at: " + libPath + "\n" +
                    "Please follow the README instructions to build and install the library."
                );
            }
        } else if (modelType.equals("OPENAI")) {
            if (!config.startsWith("sk-")) {
                throw new IllegalArgumentException("Invalid OpenAI API key format (should start with 'sk-')");
            }
        } else {
            throw new IllegalArgumentException("Invalid model type. Use OPENAI or LLAMA");
        }
    }
}
