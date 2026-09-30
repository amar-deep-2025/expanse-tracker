package com.amar.fullstack.expanse_tracker_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    @Value("${openrouter.api.key}")
    private String apiKey;

    public AiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://openrouter.ai/api/v1")
                .build();
    }

    public String ask(String prompt) {

        try {

            Map<String, Object> request = Map.of(
                    "model", "openrouter/free",

                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content",
                                    """
                                    You are a helpful financial assistant.

                                    Answer the user's question using only the financial
                                    information provided in the prompt.

                                    IMPORTANT:
                                    - Never reveal your reasoning or thinking process.
                                    - Never describe how you analyzed the data.
                                    - Never output phrases such as "Here's a thinking process",
                                      "Let me analyze", "I need to analyze", or "We need to".
                                    - Do not invent financial information.
                                    - Give the final answer directly.
                                    - Keep the answer concise and easy to understand.
                                    """
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    ),

                    "temperature", 0.1,
                    "max_tokens", 100
            );

            Map<?, ?> response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .flatMap(errorBody ->
                                            reactor.core.publisher.Mono.error(
                                                    new RuntimeException(
                                                            "OpenRouter API Error: " + errorBody
                                                    )
                                            )
                                    )
                    )
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            if (response == null) {
                return "AI response unavailable";
            }

            Object choicesObject = response.get("choices");

            if (!(choicesObject instanceof List<?> choices)
                    || choices.isEmpty()) {
                return "AI response unavailable";
            }

            Object choiceObject = choices.get(0);

            if (!(choiceObject instanceof Map<?, ?> choice)) {
                return "AI response unavailable";
            }

            Object messageObject = choice.get("message");

            if (!(messageObject instanceof Map<?, ?> message)) {
                return "AI response unavailable";
            }

            Object contentObject = message.get("content");

            if (contentObject == null) {
                return "AI response unavailable";
            }

            String result = contentObject.toString().trim();

            if (result.isEmpty()) {
                return "AI response unavailable";
            }

            // Remove markdown formatting if returned by the model
            result = result
                    .replace("```", "")
                    .replace("**", "")
                    .trim();

            // Prevent accidental thinking/reasoning output
            if (result.startsWith("Here's a thinking process")
                    || result.startsWith("Here is a thinking process")
                    || result.startsWith("Thinking process:")
                    || result.startsWith("Let me analyze")
                    || result.startsWith("I need to analyze")
                    || result.startsWith("We need to analyze")) {

                return "Please try asking your question again.";
            }

            return result;

        } catch (Exception e) {

            System.err.println("AI Service Error: " + e.getMessage());

            return "AI response unavailable";
        }
    }
}