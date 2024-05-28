package com.ecaf.ecafclientjava.technique;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;

public class HttpService {

    private final HttpClient client;
    private final Consumer<JsonNode> callback;

    public HttpService(Consumer<JsonNode> callback) {
        this.client = HttpClient.newHttpClient();
        this.callback = callback;
    }

    public void sendAsyncPostRequest(String requestBody, Consumer<JsonNode> callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl()+"auth/login")) // Remplacez par votre URL d'API
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(HttpResponse::body)
                .thenApply(this::parseJson)
                .thenAccept(callback)
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> callback.accept(null));
                    return null;
                });
    }

    private JsonNode parseJson(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
