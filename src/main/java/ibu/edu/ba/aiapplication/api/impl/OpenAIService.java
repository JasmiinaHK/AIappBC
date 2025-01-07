package ibu.edu.ba.aiapplication.api.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;
    private final OpenAiService openAiService;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.openAiService = new OpenAiService(apiKey);
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("OpenAI API key is missing or empty");
            throw new IllegalStateException("OpenAI API key is required");
        }
    }

    public String generateTaskContent(String subject, String grade, String lessonUnit, String materialType) {
        try {
            String prompt = constructPrompt(subject, grade, lessonUnit, materialType);
            
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-3.5-turbo");
            
            JsonArray messages = new JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            messages.add(message);
            
            requestBody.add("messages", messages);
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 2000);

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), requestBody.toString());
            
            Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Error from OpenAI API: " + response.code());
                    throw new IOException("Unexpected response code: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                return extractContentFromResponse(jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Error generating content with OpenAI", e);
            throw new RuntimeException("Failed to generate content", e);
        }
    }

    public String generateContent(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "You are a helpful educational content generator."));
        messages.add(new ChatMessage("user", prompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();

        try {
            return openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            logger.error("Error generating content with OpenAI: " + e.getMessage(), e);
            return "Error generating content. Please try again later.";
        }
    }

    private String constructPrompt(String subject, String grade, String lessonUnit, String materialType) {
        return String.format(
            "Create educational content for:\n" +
            "Subject: %s\n" +
            "Grade Level: %s\n" +
            "Lesson Unit: %s\n" +
            "Material Type: %s\n\n" +
            "Please provide comprehensive and engaging content suitable for the specified grade level.",
            subject, grade, lessonUnit, materialType
        );
    }

    private String extractContentFromResponse(JsonObject response) {
        try {
            return response.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (Exception e) {
            logger.error("Error extracting content from OpenAI response: " + e.getMessage(), e);
            throw new RuntimeException("Failed to extract content from response", e);
        }
    }
}
