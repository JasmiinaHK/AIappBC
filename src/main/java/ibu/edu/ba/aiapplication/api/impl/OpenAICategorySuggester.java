package ibu.edu.ba.aiapplication.api.impl;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import ibu.edu.ba.aiapplication.api.CategorySuggester;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

public class OpenAICategorySuggester implements CategorySuggester {
    private final OpenAiService openAiService;

    public OpenAICategorySuggester(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String suggestCategory(String description) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a helpful assistant that suggests categories for tasks."));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), "Suggest a category for the following task description: " + description));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(10)
                .build();

        return openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0)
                .getMessage()
                .getContent().trim();
    }
}
