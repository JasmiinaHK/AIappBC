package ibu.edu.ba.aiapplication.api.impl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import ibu.edu.ba.aiapplication.api.CategorySuggester;

@Service
public class OpenAICategorySuggester implements CategorySuggester {
    private final OpenAiService openAiService;

    public OpenAICategorySuggester(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String suggestCategory(String description) {
        String prompt = "Suggest a category for the following task description: " + description;
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(10)
                .build();
        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }
}
