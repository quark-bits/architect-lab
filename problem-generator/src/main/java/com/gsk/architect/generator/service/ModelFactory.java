package com.gsk.architect.generator.service;

import java.nio.file.Path;

public class ModelFactory {
    public static LanguageModelService createModel(ModelType type, String config) {
        return switch (type) {
            case OPENAI -> new OpenAIService(config);  // config is API key
            case LLAMA -> new LlamaService(Path.of(config));  // config is model path
        };
    }

    public enum ModelType {
        OPENAI,
        LLAMA
    }
}
