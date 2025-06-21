package com.gsk.architect.generator.service;

import java.nio.file.Path;
import java.nio.file.Files;

public class ModelFactory {
    public static LanguageModelService createModel(ModelType type, String config) {
        if (config == null || config.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration cannot be empty");
        }

        return switch (type) {
            case OPENAI -> {
                if (!config.startsWith("sk-")) {
                    throw new IllegalArgumentException("Invalid OpenAI API key format");
                }
                yield new OpenAIService(config);
            }
            case LLAMA -> {
                Path modelPath = Path.of(config);
                if (!Files.exists(modelPath)) {
                    throw new IllegalArgumentException("Llama model file not found at: " + modelPath);
                }
                if (!Files.isReadable(modelPath)) {
                    throw new IllegalArgumentException("Llama model file is not readable: " + modelPath);
                }

                // Verify llama library location
                Path libPath = Path.of(System.getProperty("user.home"), "llama-models/.llama/lib/libllama.dylib");
                if (!Files.exists(libPath)) {
                    throw new IllegalArgumentException("Llama library not found at: " + libPath +
                        "\nPlease follow the README instructions to build and install the library.");
                }

                yield new LlamaService(modelPath);
            }
        };
    }

    public enum ModelType {
        OPENAI,
        LLAMA
    }
}
