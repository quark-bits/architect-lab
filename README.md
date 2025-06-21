# Architect Lab

A Java-based application for generating software architecture problems and exercises using different language models (OpenAI or Llama).

## Prerequisites

- Java 8 or higher (64-bit JDK required)
- Maven 3.6 or higher
- For OpenAI: Valid API key
- For Llama:
  - CMake 3.12 or higher
  - A C++ compiler (Clang on macOS)
  - llama.cpp library (build instructions below)
  - GGUF format model file

## Setup Instructions

### Installing Build Tools

1. Install Homebrew (if not already installed):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. Install required tools:
   ```bash
   brew install cmake
   xcode-select --install  # Install Clang and developer tools
   ```

### Setting up Llama

1. Create the required directories:
   ```bash
   mkdir -p ~/llama-models/.llama/lib
   ```

2. Build llama.cpp (CPU version):
   ```bash
   cd ~/llama-models
   git clone https://github.com/ggerganov/llama.cpp.git
   cd llama.cpp
   mkdir build && cd build
   cmake -DBUILD_SHARED_LIBS=ON -DLLAMA_METAL=OFF ..
   make
   cp libllama.dylib ~/llama-models/.llama/lib/
   ```

3. Download a GGUF model file and place it in the `~/llama-models` directory

## Building the Application

```bash
git clone <repository-url>
cd architect-lab
mvn clean package
```

## Running the Application

### Using OpenAI

```bash
java -jar problem-generator/target/problem-generator-1.0-SNAPSHOT.jar OPENAI sk-your-api-key GENERIC
```

### Using Llama

```bash
LLAMA_METAL=0 java -Xmx4g -jar problem-generator/target/problem-generator-1.0-SNAPSHOT.jar LLAMA ~/llama-models/your-model.gguf GENERIC
```

### Parameters
- MODEL_TYPE: `OPENAI` or `LLAMA`
- CONFIG: 
  - For OpenAI: Your API key
  - For Llama: Path to your GGUF model file
- DOMAIN: `ECOMMERCE`, `VIDEO_STREAMING`, `APPLE`, or `GENERIC`

## Memory Requirements

Memory requirements depend on the model size:
- 1.7B model: 4GB minimum (-Xmx4g)
- 7B model: 8GB minimum (-Xmx8g)
- 13B model: 16GB minimum (-Xmx16g)
- 30B+ models: 32GB minimum (-Xmx32g)

## Known Issues and Troubleshooting

### Metal (GPU) Support
- Metal support is currently disabled due to stability issues
- Use CPU-only mode by setting `LLAMA_METAL=0`

### Common Issues

1. "Library not found" error:
   - Ensure libllama.dylib is in `~/llama-models/.llama/lib/`
   - Rebuild the library following the setup instructions

2. Segmentation fault during model loading:
   - Verify you're using the correct memory settings for your model size
   - Ensure you're using `LLAMA_METAL=0` environment variable
   - Try using a smaller model for testing

3. Model loading errors:
   - Verify the model file is in GGUF format
   - Check file permissions and paths
   - Ensure sufficient memory is allocated

## Domains

The application supports generating architecture problems in the following domains:

- **ECOMMERCE**: E-commerce platform design problems
- **VIDEO_STREAMING**: Video streaming service architecture
- **APPLE**: Apple-style system design questions
- **GENERIC**: General distributed systems design problems

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

[Specify your license here]
