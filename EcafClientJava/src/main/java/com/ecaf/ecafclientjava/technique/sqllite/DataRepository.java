package com.ecaf.ecafclientjava.technique.sqllite;

import com.ecaf.ecafclientjava.entites.Ressource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    public void saveRessource(Ressource ressource) {
        String sql = "INSERT INTO ressource (nom, type, quantite, emplacement) VALUES (?, ?, ?, ?)";

        try (Connection conn = SQLiteHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEmplacement());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRessource(Ressource ressource) {
        String sql = "UPDATE ressource SET nom = ?, type = ?, quantite = ?, emplacement = ? WHERE id = ?";

        try (Connection conn = SQLiteHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEmplacement());
            pstmt.setInt(5, ressource.getRessourceID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteRessource(int id) {
        String sql = "DELETE FROM ressource WHERE id = ?";

        try (Connection conn = SQLiteHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Ressource> getAllRessources() {
        List<Ressource> ressources = new ArrayList<>();
        String sql = "SELECT * FROM ressource";

        try (Connection conn = SQLiteHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ressource ressource = new Ressource(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("emplacement")
                );
                ressources.add(ressource);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ressources;
    }
}

