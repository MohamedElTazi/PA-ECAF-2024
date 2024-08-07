package com.ecaf.ecafclientjava.technique.sqllite;

import com.ecaf.ecafclientjava.entites.*;
import com.ecaf.ecafclientjava.technique.HttpResponseWrapper;
import com.ecaf.ecafclientjava.technique.HttpService;
import com.ecaf.ecafclientjava.technique.InstantTypeAdapter;
import com.ecaf.ecafclientjava.technique.Urlapi;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class SyncManager {
    public class JsonUtil {
        public static String prepareTacheRequestBody(Tache tache) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

            String dateDebutStr = tache.getDateDebut() != null ? formatter.format(tache.getDateDebut()) : null;
            String dateFinStr = tache.getDateFin() != null ? formatter.format(tache.getDateFin()) : null;

            return String.format("{\"description\":\"%s\", \"dateDebut\":\"%s\", \"dateFin\":\"%s\", \"statut\":\"%s\", \"responsable\":%d, \"ressource\":%d, \"sync_status\":\"%s\"}",
                    tache.getDescription(), dateDebutStr, dateFinStr, tache.getStatut(), tache.getResponsableId(), tache.getRessourceId(), tache.getSync_status());
        }

        public static String prepareTacheRequestBodyPatch(Tache tache) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

            String dateDebutStr = tache.getDateDebut() != null ? formatter.format(tache.getDateDebut()) : null;
            String dateFinStr = tache.getDateFin() != null ? formatter.format(tache.getDateFin()) : null;

            return String.format("{\"id\":%d, \"description\":\"%s\", \"dateDebut\":\"%s\", \"dateFin\":\"%s\", \"statut\":\"%s\", \"responsable\":%d, \"ressource\":%d,  \"sync_status\":\"%s\"}",
                    tache.getTacheID(), tache.getDescription(), dateDebutStr, dateFinStr, tache.getStatut(), tache.getResponsableId(), tache.getRessourceId(), tache.getSync_status());
        }


        public static String prepareRessourceRequestBody(Ressource ressource) {
            return String.format("{\"nom\":\"%s\", \"type\":\"%s\", \"quantite\":%d, \"emplacement\":\"%s\", \"sync_status\":\"%s\"}",
                    ressource.getNom(), ressource.getType(), ressource.getQuantite(), ressource.getEmplacement(), ressource.getSync_status());
        }

        public static String prepareRessourceRequestBodyPatch(Ressource ressource) {
            return String.format("{\"id\":%d, \"nom\":\"%s\", \"type\":\"%s\", \"quantite\":%d, \"emplacement\":\"%s\", \"sync_status\":\"%s\"}",
                    ressource.getRessourceID(), ressource.getNom(), ressource.getType(), ressource.getQuantite(), ressource.getEmplacement(), ressource.getSync_status());
        }

    }

    private final HttpService httpService = new HttpService();
    private static final OkHttpClient client = new OkHttpClient();

    public void syncDisplayedTables() {
        syncTable("ag", Urlapi.BASE_URL.getUrl()+"ags");
        syncTable("user", Urlapi.BASE_URL.getUrl()+"users");
        syncTable("evenement", Urlapi.BASE_URL.getUrl()+"evenements");
        syncTable("tache", Urlapi.BASE_URL.getUrl()+"taches");
        syncTable("ressource", Urlapi.BASE_URL.getUrl()+"ressources");


    }

    private void syncTable(String tableName, String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                // Assurez-vous de parser et d'insérer les données dans SQLite
                insertDataIntoTable(tableName, jsonData);
                System.out.println("Données synchronisées pour la table " + tableName);
            } else {
                System.out.println("Échec de la synchronisation pour la table " + tableName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void insertDataIntoTable(String tableName, String jsonData) {
        System.out.println("jsonData received for table " + tableName + ": " + jsonData);

        try (Connection conn = SQLiteHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName)) {
            pstmt.executeUpdate(); // Supprimez les anciennes données

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .create();

            // Déterminez si jsonData est un tableau ou un objet
            JsonElement jsonElement = JsonParser.parseString(jsonData);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray;

                if (tableName.equals("ag") && jsonObject.has("Ags")) {
                    jsonArray = jsonObject.getAsJsonArray("Ags");
                } else if (tableName.equals("user") && jsonObject.has("Users")) {
                    jsonArray = jsonObject.getAsJsonArray("Users");
                } else if (tableName.equals("evenement") && jsonObject.has("Evenements")) {
                    jsonArray = jsonObject.getAsJsonArray("Evenements");
                } else if (tableName.equals("tache") && jsonObject.has("Taches")) {
                    jsonArray = jsonObject.getAsJsonArray("Taches");
                } else if (tableName.equals("ressource") && jsonObject.has("Ressources")) {
                    jsonArray = jsonObject.getAsJsonArray("Ressources");
                } else {
                    System.out.println("Format JSON non pris en charge pour la table " + tableName);
                    return;
                }

                parseAndInsertData(tableName, jsonArray, gson, conn);
            } else {
                System.out.println("Format JSON non pris en charge pour la table " + tableName);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void parseAndInsertData(String tableName, JsonArray jsonArray, Gson gson, Connection conn) throws SQLException {
        if (tableName.equals("ag")) {
            List<AG> ags = Arrays.asList(gson.fromJson(jsonArray, AG[].class));
            for (AG ag : ags) {
                String sql = "INSERT INTO ag (id, nom, date, description, type, quorum) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, ag.getAgId());
                    pstmt2.setString(2, ag.getNom());
                    pstmt2.setTimestamp(3, Timestamp.from(ag.getDate()));
                    pstmt2.setString(4, ag.getDescription());
                    pstmt2.setString(5, ag.getType());
                    pstmt2.setInt(6, ag.getQuorum());
                    pstmt2.executeUpdate();
                    System.out.println("Données insérées dans la table ag : " + ag.getNom());
                }
            }
        } else if (tableName.equals("evenement")) {
            List<Evenement> evenements = Arrays.asList(gson.fromJson(jsonArray, Evenement[].class));
            for (Evenement evenement : evenements) {
                String sql = "INSERT INTO evenement (id, nom, date, description, lieu, nbPlace, estReserve) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, evenement.getEventId());
                    pstmt2.setString(2, evenement.getNom());
                    pstmt2.setTimestamp(3, Timestamp.from(evenement.getDate()));
                    pstmt2.setString(4, evenement.getDescription());
                    pstmt2.setString(5, evenement.getLieu());

                    pstmt2.executeUpdate();
                    System.out.println("Données insérées dans la table evenement : " + evenement.getNom());
                }
            }
        } else if (tableName.equals("tache")) {
            List<Tache> taches = Arrays.asList(gson.fromJson(jsonArray, Tache[].class));
            for (Tache tache : taches) {
                String sql = "INSERT INTO tache (id, description, dateDebut, dateFin, statut, responsableId, ressourceId) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, tache.getTacheID());
                    pstmt2.setString(2, tache.getDescription());
                    pstmt2.setTimestamp(3, Timestamp.from(tache.getDateDebut()));
                    pstmt2.setTimestamp(4, Timestamp.from(tache.getDateFin()));
                    pstmt2.setString(5, tache.getStatut());
                    pstmt2.setInt(6, tache.getResponsable().getId());
                    pstmt2.setInt(7, tache.getRessource().getRessourceID());
                    pstmt2.executeUpdate();
                    System.out.println("Données insérées dans la table tache : " + tache.getDescription());
                }
            }
        } else if (tableName.equals("ressource")) {
            List<Ressource> ressources = Arrays.asList(gson.fromJson(jsonArray, Ressource[].class));
            for (Ressource ressource : ressources) {
                String sql = "INSERT INTO ressource (id, nom, type, quantite, emplacement) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, ressource.getRessourceID());
                    pstmt2.setString(2, ressource.getNom());
                    pstmt2.setString(3, ressource.getType());
                    pstmt2.setInt(4, ressource.getQuantite());
                    pstmt2.setString(5, ressource.getEmplacement());
                    pstmt2.executeUpdate();
                    System.out.println("Données insérées dans la table ressource : " + ressource.getNom());
                }
            }
        } else if (tableName.equals("user")) {
            List<User> users = Arrays.asList(gson.fromJson(jsonArray, User[].class));
            for (User user : users) {
                String sql = "INSERT INTO user (id, nom, prenom, email, motDePasse, role, profession, numTel, dateInscription, estBenevole, estEnLigne) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                    pstmt2.setInt(1, user.getId());
                    pstmt2.setString(2, user.getNom());
                    pstmt2.setString(3, user.getPrenom());
                    pstmt2.setString(4, user.getEmail());
                    pstmt2.setString(5, user.getMotDePasse());
                    pstmt2.setString(6, user.getRole());
                    pstmt2.setBoolean(7, user.isEstBenevole());
                    pstmt2.setTimestamp(8, Timestamp.from(user.getDateInscription()));
                    pstmt2.setBoolean(9, user.isEstEnLigne());
                    pstmt2.executeUpdate();
                    System.out.println("Données insérées dans la table user : " + user.getNom());
                }
            }
        }
    }



    public void syncData() throws IOException, InterruptedException {
        if (NetworkUtil.isOnline()) {
            // Synchroniser les tâches
            List<Tache> newTaches = SQLiteHelper.getTachesBySyncStatus(SyncStatus.NEW.getStatus());
            for (Tache tache : newTaches) {
                String requestBody = JsonUtil.prepareTacheRequestBody(tache);
                HttpResponseWrapper responseWrapper = httpService.sendPostRequest("taches", requestBody);
                if (responseWrapper.getStatusCode() == 201) {
                    SQLiteHelper.updateTacheSyncStatus(tache.getTacheID(), SyncStatus.SYNCED.getStatus());
                }
            }

            List<Tache> updatedTaches = SQLiteHelper.getTachesBySyncStatus(SyncStatus.UPDATED.getStatus());
            for (Tache tache : updatedTaches) {
                String requestBody = JsonUtil.prepareTacheRequestBody(tache);
                HttpResponseWrapper responseWrapper = httpService.sendPatchRequest("taches/" + tache.getTacheID(), requestBody);
                if (responseWrapper.getStatusCode() == 200) {
                    SQLiteHelper.updateTacheSyncStatus(tache.getTacheID(), SyncStatus.SYNCED.getStatus());
                }
            }

            List<Tache> deletedTaches = SQLiteHelper.getTachesBySyncStatus(SyncStatus.DELETED.getStatus());
            for (Tache tache : deletedTaches) {
                HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("taches/" + tache.getTacheID(), "");
                if (responseWrapper.getStatusCode() == 200) {
                    SQLiteHelper.deleteTachePermanently(tache.getTacheID());
                }
            }

            // Synchroniser les ressources
            List<Ressource> newRessources = SQLiteHelper.getRessourcesBySyncStatus(SyncStatus.NEW.getStatus());
            for (Ressource ressource : newRessources) {
                String requestBody = JsonUtil.prepareRessourceRequestBody(ressource);
                HttpResponseWrapper responseWrapper = httpService.sendPostRequest("ressources", requestBody);
                if (responseWrapper.getStatusCode() == 201) {
                    SQLiteHelper.updateRessourceSyncStatus(ressource.getRessourceID(), SyncStatus.SYNCED.getStatus());
                }
            }
            List<Ressource> updatedRessources = SQLiteHelper.getRessourcesBySyncStatus(SyncStatus.UPDATED.getStatus());
            for (Ressource ressource : updatedRessources) {
                String requestBody = JsonUtil.prepareRessourceRequestBody(ressource);
                HttpResponseWrapper responseWrapper = httpService.sendPatchRequest("ressources/" + ressource.getRessourceID(), requestBody);
                if (responseWrapper.getStatusCode() == 200) {
                    SQLiteHelper.updateRessourceSyncStatus(ressource.getRessourceID(), SyncStatus.SYNCED.getStatus());
                }
            }

             List<Ressource> deletedRessources = SQLiteHelper.getRessourcesBySyncStatusDelete(SyncStatus.DELETED.getStatus());
            for (Ressource ressource : deletedRessources) {
                HttpResponseWrapper responseWrapper = httpService.sendDeleteRequest("ressources/" + ressource.getRessourceID(), "");
                if (responseWrapper.getStatusCode() == 200) {
                    SQLiteHelper.deleteRessourcePermanently(ressource.getRessourceID());
                }
            }
        }
    }



}
