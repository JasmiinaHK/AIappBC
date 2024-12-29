package ibu.edu.ba.aiapplication.core.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        logger.info("Initializing OpenAIService");
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("OpenAI API key is missing or empty");
            throw new IllegalStateException("OpenAI API key is required");
        }
        logger.info("OpenAIService initialized successfully");
    }

    public String generateTaskContent(String subject, String grade, String lessonUnit, String materialType) {
        logger.info("Generating content for subject: {}, grade: {}, unit: {}, type: {}", 
                   subject, grade, lessonUnit, materialType);
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1000);

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", String.format(
            "Create educational content for:\nSubject: %s\nGrade: %s\nLesson Unit: %s\nMaterial Type: %s\n" +
            "Provide a well-structured, age-appropriate content that aligns with educational standards.",
            subject, grade, lessonUnit, materialType
        ));

        JsonObject[] messages = new JsonObject[]{message};
        requestBody.add("messages", gson.toJsonTree(messages));

        String jsonBody = gson.toJson(requestBody);
        logger.debug("Request body: {}", jsonBody);

        RequestBody body = RequestBody.create(
            jsonBody,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(OPENAI_API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();

        try {
            logger.info("Sending request to OpenAI API");
            Response response = client.newCall(request).execute();
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                logger.error("OpenAI API call failed. Status: {}, Error: {}", response.code(), errorBody);
                throw new RuntimeException("OpenAI API call failed: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            logger.debug("Response from OpenAI: {}", responseBody);
            
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            String content = jsonResponse.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
                
            logger.info("Content generated successfully");
            return content;
        } catch (IOException e) {
            logger.error("Failed to generate content", e);
            throw new RuntimeException("Failed to generate content: " + e.getMessage());
        }
    }
}
