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

    public HttpService(Consumer<HttpResponseWrapper> callback) {
        this.client = HttpClient.newHttpClient();
    }

    public void sendAsyncPostRequest(String url, String requestBody, Consumer<HttpResponseWrapper> callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl()+url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(httpResponse -> {
                    int statusCode = httpResponse.statusCode();
                    JsonNode jsonNode = parseJson(httpResponse.body());
                    return new HttpResponseWrapper(jsonNode, statusCode);
                })
                .thenAccept(callback)
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> callback.accept(new HttpResponseWrapper(null, 500)));
                    return null;
                });
    }

    public void sendAsyncGetRequest(String url, Consumer<HttpResponseWrapper> callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(httpResponse -> {
                    int statusCode = httpResponse.statusCode();
                    JsonNode jsonNode = parseJson(httpResponse.body());
                    return new HttpResponseWrapper(jsonNode, statusCode);
                })
                .thenAccept(callback)
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> callback.accept(new HttpResponseWrapper(null, 500)));
                    return null;
                });
    }

    public void sendAsyncPatchRequest(String url, String requestBody, Consumer<HttpResponseWrapper> callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + url))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(httpResponse -> {
                    int statusCode = httpResponse.statusCode();
                    JsonNode jsonNode = parseJson(httpResponse.body());
                    return new HttpResponseWrapper(jsonNode, statusCode);
                })
                .thenAccept(callback)
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> callback.accept(new HttpResponseWrapper(null, 500)));
                    return null;
                });
    }

    public void sendAsyncDeleteRequest(String url, Consumer<HttpResponseWrapper> callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + url))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(httpResponse -> {
                    int statusCode = httpResponse.statusCode();
                    JsonNode jsonNode = parseJson(httpResponse.body());
                    return new HttpResponseWrapper(jsonNode, statusCode);
                })
                .thenAccept(callback)
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> callback.accept(new HttpResponseWrapper(null, 500)));
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
