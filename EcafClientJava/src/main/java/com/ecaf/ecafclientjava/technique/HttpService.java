package com.ecaf.ecafclientjava.technique;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;


import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.entites.Tache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class HttpService {

    private final HttpClient client;

    public HttpService() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponseWrapper sendPostRequest(String endpoint,String requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        JsonNode jsonNode = parseJson(response.body());

        return new HttpResponseWrapper(jsonNode, statusCode);
    }

    public HttpResponseWrapper sendGetRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        JsonNode jsonNode = parseJson(response.body());

        return new HttpResponseWrapper(jsonNode, statusCode);
    }

    public HttpResponseWrapper sendPatchRequest(String endpoint, String requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + endpoint))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        JsonNode jsonNode = parseJson(response.body());

        return new HttpResponseWrapper(jsonNode, statusCode);
    }

    public HttpResponseWrapper sendDeleteRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + endpoint))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        JsonNode jsonNode = parseJson(response.body());

        return new HttpResponseWrapper(jsonNode, statusCode);
    }

    public List<Evenement> getAllEvenement() throws IOException, InterruptedException {
        String endpoint = "evenements";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<Evenement> evenements = null;

        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            JsonElement responseBody = JsonParser.parseString(responseWrapper.getBody().toString());
            EventResponse eventResponse = gson.fromJson(responseBody, EventResponse.class);
            evenements = eventResponse.getEvent();
        }
        return evenements;
    }
    public List<Tache> getAllTache() throws IOException, InterruptedException {
        String endpoint = "taches";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<Tache> taches = null;

        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            JsonElement responseBody = JsonParser.parseString(responseWrapper.getBody().toString());
            TacheResponse tacheResponse = gson.fromJson(responseBody, TacheResponse.class);
            taches = tacheResponse.getTache();
        }
        return taches;
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

