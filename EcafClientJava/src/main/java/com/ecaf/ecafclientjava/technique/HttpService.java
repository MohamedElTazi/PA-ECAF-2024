package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.*;
import com.ecaf.ecafclientjava.technique.sqllite.NetworkUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecaf.ecafclientjava.technique.sqllite.SQLiteHelper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HttpService {

    private final HttpClient client;

    public HttpService() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponseWrapper sendPostRequest(String endpoint, String requestBody) throws IOException, InterruptedException {
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

    public HttpResponseWrapper sendDeleteRequest(String endpoint, String requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Urlapi.BASE_URL.getUrl() + endpoint))
                .header("Content-Type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        JsonNode jsonNode = parseJson(response.body());

        return new HttpResponseWrapper(jsonNode, statusCode);
    }

    public List<User> getUsersByRole(String role) throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            // Chargez les données depuis SQLite lorsque vous êtes offline
            return SQLiteHelper.getUsersByRole(role);
        }

        String endpoint = "users?role=" + role;
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<User> users = new ArrayList<>();
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            UserResponse userResponse = gson.fromJson(responseBody, UserResponse.class);
            users = userResponse.getUsers();
        }
        return users;
    }

    public List<User> getUsersByOnlineStatus() throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            // Chargez les données depuis SQLite lorsque vous êtes offline
            return SQLiteHelper.getUsersByOnlineStatus();
        }

        String endpoint = "users?estEnLigne=true";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<User> users = new ArrayList<>();
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            UserResponse userResponse = gson.fromJson(responseBody, UserResponse.class);
            users = userResponse.getUsers();
        }
        return users;
    }

    public List<Tache> getAllTaches() throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            // Chargez les données depuis SQLite lorsque vous êtes offline
            return SQLiteHelper.getAllTaches();
        }

        String endpoint = "taches";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<Tache> taches = null;
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            TacheResponse tacheResponse = gson.fromJson(responseBody, TacheResponse.class);
            taches = tacheResponse.getTaches();
        }
        return taches;
    }

    public List<Ressource> getAllRessources() throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            // Chargez les données depuis SQLite lorsque vous êtes offline
            return SQLiteHelper.getAllRessources();
        }

        String endpoint = "ressources";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<Ressource> ressources = null;
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder().create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            RessourceResponse ressourceResponse = gson.fromJson(responseBody, RessourceResponse.class);
            ressources = ressourceResponse.getRessources();
        }
        return ressources;
    }

    public List<Evenement> getAllEvenement() throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            return SQLiteHelper.getAllEvenement();
        }

        String endpoint = "evenements";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<Evenement> evenements = null;
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            EventResponse eventResponse = gson.fromJson(responseBody, EventResponse.class);
            evenements = eventResponse.getEvent();
        }
        return evenements;
    }

    public List<AG> getAllAG() throws IOException, InterruptedException {
        if (!NetworkUtil.isOnline()) {
            // Chargez les données depuis SQLite lorsque vous êtes offline
            return SQLiteHelper.getAllAG();
        }

        String endpoint = "ags";
        HttpResponseWrapper responseWrapper = sendGetRequest(endpoint);
        List<AG> ags = null;
        if (responseWrapper.getStatusCode() == 200) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();
            String responseBody = responseWrapper.getBody().toString(); // Convert JsonNode to String
            AgResponse agResponse = gson.fromJson(responseBody, AgResponse.class);
            ags = agResponse.getAgs();
        }
        return ags;
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
