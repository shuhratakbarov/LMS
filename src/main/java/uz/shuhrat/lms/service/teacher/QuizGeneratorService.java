package uz.shuhrat.lms.service.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizGeneratorService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;

    public String generateQuiz(String topic, String content, int amount, String difficulty, String areas) {
        String prompt = """
            Generate multiple-choice quiz questions about the topic below.
            Each question must have 4 options and one correct answer.
            Return ONLY valid JSON in this format:
            [
              {"question":"...", "options":["A","B","C","D"], "correctAnswer":"B"},
              ...
            ]
            Topic: %s
            Content: %s
            Amount: %s
            Difficulty: %s
            Areas: %s
            """.formatted(topic, content, amount, difficulty, areas);

        String url = endpoint ;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> requestBody = Map.of(
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(  // Change to List
                                        Map.of(
                                                "type", "text",
                                                "text", prompt
                                        )
                                )
                        )
                ),
                "max_completion_tokens", 20000
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    System.out.println((String) message.get("content"));
                    return (String) message.get("content");
                }
            }

            return "[]"; // Empty response fallback
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate questions: " + e.getMessage());
        }
    }
}