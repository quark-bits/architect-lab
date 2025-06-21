package com.gsk.architect.generator.service;

import com.sun.jna.*;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.nio.file.Files;

@Slf4j
public class LlamaService implements LanguageModelService {
    private static final float TEMPERATURE = 0.7f;
    private static final int MAX_LENGTH = 2048;
    private final Path libraryPath;
    private final Path modelPath;
    private static volatile boolean libraryLoaded = false;
    private static final Object lock = new Object();

    @Structure.FieldOrder({
        "n_gpu_layers",
        "main_gpu",
        "tensor_split",
        "vocab_only",
        "use_mmap",
        "use_mlock",
        "seed",
        "n_ctx",
        "n_batch",
        "n_threads",
        "n_threads_batch"
    })
    public static class llama_model_params extends Structure {
        public int n_gpu_layers;
        public int main_gpu;
        public Pointer tensor_split;
        public boolean vocab_only;
        public boolean use_mmap;
        public boolean use_mlock;
        public int seed;
        public int n_ctx;
        public int n_batch;
        public int n_threads;
        public int n_threads_batch;

        public static llama_model_params defaultParams() {
            log.info("Creating default model parameters");
            llama_model_params params = new llama_model_params();
            params.n_gpu_layers = -1;      // Disable GPU/Metal (-1 means CPU only)
            params.main_gpu = 0;
            params.tensor_split = null;
            params.vocab_only = false;
            params.use_mmap = true;
            params.use_mlock = false;
            params.seed = -1;
            params.n_ctx = 512;            // Reduced context size
            params.n_batch = 512;          // Default batch size
            params.n_threads = Runtime.getRuntime().availableProcessors();
            params.n_threads_batch = Runtime.getRuntime().availableProcessors();
            return params;
        }
    }

    public interface LlamaCpp extends Library {
        LlamaCpp INSTANCE = Native.load("llama", LlamaCpp.class);

        void llama_backend_init(boolean numa);
        void llama_backend_free();
        Pointer llama_load_model_from_file(String path, llama_model_params params);
        void llama_free_model(Pointer ctx);
    }

    public LlamaService(Path modelPath) {
        log.info("Initializing LlamaService with model path: {}", modelPath);
        this.modelPath = modelPath;
        this.libraryPath = Path.of(System.getProperty("user.home"), "llama-models/.llama/lib");

        // Disable Metal/GPU support
        System.setProperty("jna.platform.library.path", libraryPath.toString());
        System.setProperty("LLAMA_METAL", "0");

        initializeLibrary();
    }

    private void initializeLibrary() {
        synchronized (lock) {
            if (!libraryLoaded) {
                try {
                    validateLibrary();
                    log.info("Initializing Llama backend...");
                    LlamaCpp.INSTANCE.llama_backend_init(false);
                    libraryLoaded = true;
                    log.info("Llama backend initialized successfully");
                } catch (Exception e) {
                    log.error("Failed to initialize Llama backend", e);
                    throw new RuntimeException("Failed to initialize Llama", e);
                }
            }
        }
    }

    private void validateLibrary() {
        try {
            log.info("Validating library at path: {}", libraryPath);
            if (!Files.exists(libraryPath)) {
                throw new RuntimeException("Llama.cpp library directory not found at " + libraryPath);
            }

            Path dylibPath = libraryPath.resolve("libllama.dylib");
            log.info("Checking for library file at: {}", dylibPath);
            if (!Files.exists(dylibPath)) {
                throw new RuntimeException("libllama.dylib not found at " + dylibPath);
            }

            if (!Files.isReadable(dylibPath)) {
                throw new RuntimeException("libllama.dylib is not readable at " + dylibPath);
            }

            log.info("Setting jna.library.path to: {}", libraryPath);
            System.setProperty("jna.library.path", libraryPath.toString());
        } catch (Exception e) {
            log.error("Error validating Llama library", e);
            throw new RuntimeException("Failed to validate Llama library: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateResponse(String prompt) {
        Pointer model = null;
        try {
            log.info("Creating model parameters");
            llama_model_params params = llama_model_params.defaultParams();

            log.info("Loading model from: {}", modelPath);
            log.info("Model parameters: n_gpu_layers={}, use_mmap={}, use_mlock={}",
                    params.n_gpu_layers, params.use_mmap, params.use_mlock);

            model = LlamaCpp.INSTANCE.llama_load_model_from_file(modelPath.toString(), params);

            if (model == null) {
                throw new RuntimeException("Failed to load Llama model from " + modelPath);
            }
            log.info("Model loaded successfully");

            // For now, just return a simple response to test if model loading works
            return "Test response - model loaded successfully";
        } catch (Exception e) {
            log.error("Error generating response from Llama", e);
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        } finally {
            if (model != null) {
                try {
                    log.info("Freeing model resources");
                    LlamaCpp.INSTANCE.llama_free_model(model);
                } catch (Exception e) {
                    log.error("Error freeing model resources", e);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            synchronized (lock) {
                if (libraryLoaded) {
                    log.info("Freeing Llama backend in finalizer");
                    LlamaCpp.INSTANCE.llama_backend_free();
                    libraryLoaded = false;
                }
            }
        } finally {
            super.finalize();
        }
    }
}
