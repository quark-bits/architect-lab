# Architect Lab

A Java-based application for generating software architecture problems and exercises using different language models.

## Project Structure

The project consists of two main modules:
- `problem-generator`: Core module that generates architectural problems using language models
- `lab-solutions`: Module for storing solutions to the generated problems

## Problem Generator

The problem generator module uses language models (OpenAI or Llama) to create software architecture problems across different domains.

### Supported Domains
- E-commerce
- Video Streaming
- Apple (Apple-style system design)
- Generic

### Supported Language Models
- OpenAI (using OpenAI API)
- Llama (using local Llama model)

## Setup

1. Ensure you have Java installed on your system
2. Clone the repository
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

## Usage

Run the problem generator with the following command:

```bash
java -jar problem-generator/target/problem-generator-1.0-SNAPSHOT.jar <MODEL_TYPE> <CONFIG> <DOMAIN>
```

### Parameters:
- `MODEL_TYPE`: Choose between `OPENAI` or `LLAMA`
- `CONFIG`: 
  - For OpenAI: Provide your OpenAI API key
  - For Llama: Provide the path to your Llama model file
- `DOMAIN`: Choose from `ECOMMERCE`, `VIDEO_STREAMING`, `APPLE`, or `GENERIC`

### Example:
```bash
# Using OpenAI
java -jar problem-generator/target/problem-generator-1.0-SNAPSHOT.jar OPENAI sk-your-api-key ECOMMERCE

# Using Llama
java -jar problem-generator/target/problem-generator-1.0-SNAPSHOT.jar LLAMA /path/to/llama/model GENERIC
```

## Building from Source

1. Ensure you have Maven installed
2. Clone the repository
3. Navigate to the project root
4. Run:
   ```bash
   mvn clean install
   ```

## Requirements

- Java 8 or higher
- Maven 3.6 or higher
- For OpenAI model: Valid OpenAI API key
- For Llama model: Local Llama model file
