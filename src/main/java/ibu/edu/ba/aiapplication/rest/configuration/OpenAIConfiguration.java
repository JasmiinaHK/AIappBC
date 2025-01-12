package ibu.edu.ba.aiapplication.rest.configuration;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import ibu.edu.ba.aiapplication.core.model.Material;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenAIConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIConfiguration.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public OpenAiService openAiService() {
        logger.info("Initializing OpenAI service...");
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public String generateContent(Material material) {
        OpenAiService service = openAiService();
        
        String prompt = buildPrompt(material);
        logger.info("Generated prompt for material: {}", prompt);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a helpful educational content creator."));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .temperature(0.7)
            .maxTokens(1000)
            .build();

        try {
            logger.info("Sending request to OpenAI...");
            String response = service.createChatCompletion(completionRequest)
                .getChoices().get(0)
                .getMessage()
                .getContent();
            logger.info("Received response from OpenAI: {} characters", response.length());
            return response;
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate content: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(Material material) {
        String basePrompt = String.format(
            "Create educational content for:\n" +
            "Subject: %s\n" +
            "Grade: %s\n" +
            "Topic: %s\n" +
            "Type: %s\n" +
            "Language: %s\n\n",
            material.getSubject(),
            material.getGrade(),
            material.getLessonUnit(),
            material.getMaterialType(),
            material.getLanguage()
        );

        if ("Test".equals(material.getMaterialType())) {
            return basePrompt + "Generate a test with multiple choice questions and problem-solving tasks appropriate for this grade level.";
        } else {
            return basePrompt + "Generate creative educational material with examples and interactive exercises appropriate for this grade level.";
        }
    }
}
