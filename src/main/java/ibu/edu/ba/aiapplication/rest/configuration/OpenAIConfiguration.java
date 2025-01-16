package ibu.edu.ba.aiapplication.rest.configuration;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenAIConfiguration {
    private final OpenAiService openAiService;
    private final String model;
    private final Integer maxTokens;
    private final Double temperature;

    public OpenAIConfiguration(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model:gpt-3.5-turbo}") String model,
            @Value("${openai.max-tokens:500}") Integer maxTokens,
            @Value("${openai.temperature:0.7}") Double temperature) {
        this.openAiService = new OpenAiService(apiKey);
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    public String generateContent(Material material) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
            "You are a helpful AI assistant that generates educational content. " +
            "Generate content that is appropriate for the given subject and grade level."));
        
        String prompt = String.format(
            "Generate educational content for:\nSubject: %s\nGrade: %s\nLesson Unit: %s\nMaterial Type: %s\nLanguage: %s",
            material.getSubject(),
            material.getGrade(),
            material.getLessonUnit(),
            material.getMaterialType(),
            material.getLanguage()
        );
        
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model(model)
            .messages(messages)
            .maxTokens(maxTokens)
            .temperature(temperature)
            .build();

        try {
            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate content", e);
        }
    }

    public String generateLessonUnits(String subject) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
            "You are a helpful AI assistant that generates lesson units for school subjects. " +
            "Generate 5-7 lesson units that are appropriate for the given subject. " +
            "Return ONLY the lesson unit names, separated by commas, without any additional text or formatting."));
        
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), 
            "Generate lesson units for the subject: " + subject));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model(model)
            .messages(messages)
            .maxTokens(maxTokens)
            .temperature(temperature)
            .build();

        try {
            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate lesson units: " + e.getMessage());
        }
    }
}
