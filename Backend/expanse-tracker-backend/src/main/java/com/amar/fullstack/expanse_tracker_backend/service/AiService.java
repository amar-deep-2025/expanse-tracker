package com.amar.fullstack.expanse_tracker_backend.service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://127.0.0.1:11434")
                .build();
    }

    public String ask(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "model", "phi3:mini",
                    "stream", false,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You are a financial assistant. Reply in one complete short sentence only."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "options", Map.of(
                            "temperature", 0,
                            "num_predict", 60
                    )
            );

            Map response = webClient.post()
                    .uri("/api/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(120))
                    .block();

            if (response == null || response.get("message") == null) {
                return "AI insight unavailable";
            }

            Map message = (Map) response.get("message");
            return message.get("content").toString()
                    .replace("$","")
                    .replace("dollars","")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI insight unavailable";
        }
    }
}