package ibu.edu.ba.aiapplication.api;

public interface OpenAPISuggester {
    String suggestEndpointDescription(String endpoint, String method);
    String suggestResponseExample(String endpoint, String method, String responseType);
    String suggestTags(String endpoint, String method);
}
