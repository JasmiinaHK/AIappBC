package ibu.edu.ba.aiapplication.api.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
        
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

            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            MediaType.parse("application/json"),
                            requestBody.toString()
                    ))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("OpenAI API request failed with code: " + response.code());
                    throw new RuntimeException("OpenAI API request failed with code: " + response.code());
                }

                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                return extractContentFromResponse(jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Error generating content with OpenAI: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate content", e);
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
