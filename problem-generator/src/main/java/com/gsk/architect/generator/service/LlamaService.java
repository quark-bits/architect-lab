package com.gsk.architect.generator.service;

import com.sun.jna.*;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;

@Slf4j
public class LlamaService implements LanguageModelService {
    private static final float TEMPERATURE = 0.7f;
    private static final int MAX_LENGTH = 2048;
    private final Path libraryPath;
    private final Path modelPath;

    public interface LlamaCpp extends Library {
        LlamaCpp INSTANCE = Native.load("llama", LlamaCpp.class);

        Pointer llama_load_model_from_file(String path);
        void llama_free_model(Pointer model);
        String llama_generate(Pointer model, String prompt, int maxTokens, float temp);
    }

    public LlamaService(Path modelPath) {
        this.modelPath = modelPath;
        this.libraryPath = Path.of(System.getProperty("user.home"), ".llama", "lib");
        validateLibrary();
    }

    private void validateLibrary() {
        try {
            if (!libraryPath.toFile().exists()) {
                throw new RuntimeException("Llama.cpp library not found at " + libraryPath +
                    ". Please build llama.cpp and place the shared library in this location.");
            }
            System.setProperty("jna.library.path", libraryPath.toString());
        } catch (Exception e) {
            log.error("Error validating Llama library", e);
            throw new RuntimeException("Failed to initialize Llama", e);
        }
    }

    @Override
    public String generateResponse(String prompt) {
        Pointer model = null;
        try {
            model = LlamaCpp.INSTANCE.llama_load_model_from_file(modelPath.toString());
            if (model == null) {
                throw new RuntimeException("Failed to load Llama model from " + modelPath);
            }

            String systemPrompt = "You are a senior system design interviewer at FAANG companies.";
            String fullPrompt = systemPrompt + "\n\n" + prompt;

            return LlamaCpp.INSTANCE.llama_generate(model, fullPrompt, MAX_LENGTH, TEMPERATURE);
        } catch (Exception e) {
            log.error("Error generating response from Llama", e);
            throw new RuntimeException("Failed to generate response", e);
        } finally {
            if (model != null) {
                LlamaCpp.INSTANCE.llama_free_model(model);
            }
        }
    }
}
