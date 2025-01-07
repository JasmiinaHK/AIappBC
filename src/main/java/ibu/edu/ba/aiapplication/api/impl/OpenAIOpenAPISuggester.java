package ibu.edu.ba.aiapplication.api.impl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import ibu.edu.ba.aiapplication.api.OpenAPISuggester;

@Service
public class OpenAIOpenAPISuggester implements OpenAPISuggester {
    private final OpenAiService openAiService;

    public OpenAIOpenAPISuggester(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String suggestEndpointDescription(String endpoint, String method) {
        String prompt = String.format("Suggest a clear and concise OpenAPI description for the following API endpoint - Method: %s, Path: %s. " +
                "Focus on its purpose and expected usage.", method, endpoint);
        
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(100)
                .build();
        
        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }

    @Override
    public String suggestResponseExample(String endpoint, String method, String responseType) {
        String prompt = String.format("Generate a realistic example response for API endpoint - Method: %s, Path: %s, Response Type: %s. " +
                "The response should be in a format that can be used in OpenAPI documentation.", method, endpoint, responseType);
        
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(150)
                .build();
        
        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }

    @Override
    public String suggestTags(String endpoint, String method) {
        String prompt = String.format("Suggest appropriate OpenAPI tags for categorizing this endpoint - Method: %s, Path: %s. " +
                "Return only the tag names separated by commas.", method, endpoint);
        
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(50)
                .build();
        
        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }
}