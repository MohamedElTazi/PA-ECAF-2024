package com.ecaf.ecafclientjava.technique.sqllite;

import com.ecaf.ecafclientjava.entites.*;

import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper {

    private static final String URL = "jdbc:sqlite:/home/r-mehdi/ESGI/pa/ECAF-JAR/localdb.sqlite";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static void createTables() {
        String agTable = "CREATE TABLE IF NOT EXISTS ag ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nom TEXT NOT NULL,"
                + " date DATETIME NOT NULL,"
                + " description TEXT,"
                + " type TEXT NOT NULL,"
                + " quorum INTEGER NOT NULL"
                + ");";

        String userTable = "CREATE TABLE IF NOT EXISTS user ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nom TEXT NOT NULL,"
                + " prenom TEXT NOT NULL,"
                + " email TEXT NOT NULL UNIQUE,"
                + " motDePasse TEXT,"
                + " profession TEXT,"
                + " numTel TEXT,"
                + " role TEXT NOT NULL,"
                + " dateInscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + " estBenevole BOOLEAN DEFAULT FALSE,"
                + " estEnLigne BOOLEAN DEFAULT FALSE"
                + ");";

        String evenementTable = "CREATE TABLE IF NOT EXISTS evenement ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nom TEXT NOT NULL,"
                + " date DATETIME NOT NULL,"
                + " description TEXT NOT NULL,"
                + " lieu TEXT NOT NULL,"
                + " nbPlace INTEGER,"
                + " estReserve BOOLEAN DEFAULT FALSE"
                + ");";

        String ressourceTable = "CREATE TABLE IF NOT EXISTS ressource ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nom TEXT NOT NULL,"
                + " type TEXT NOT NULL,"
                + " quantite INTEGER DEFAULT 1,"
                + " emplacement TEXT DEFAULT NULL,"
                + " sync_status TEXT DEFAULT 'synced'"
                + ");";

        String tacheTable = "CREATE TABLE IF NOT EXISTS tache ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " description TEXT NOT NULL,"
                + " dateDebut DATE NOT NULL,"
                + " dateFin DATE NOT NULL,"
                + " statut TEXT NOT NULL,"
                + " responsableId INTEGER,"
                + " ressourceId INTEGER,"
                + " sync_status TEXT DEFAULT 'synced',"
                + " FOREIGN KEY (responsableId) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                + " FOREIGN KEY (ressourceId) REFERENCES ressource(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(agTable);
            stmt.execute(userTable);
            stmt.execute(evenementTable);
            stmt.execute(ressourceTable);
            stmt.execute(tacheTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("role"),
                        rs.getTimestamp("dateInscription").toInstant(),
                        rs.getBoolean("estBenevole"),
                        rs.getBoolean("estEnLigne")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public static List<User> getUsersByOnlineStatus() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE estEnLigne = 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("role"),
                        rs.getTimestamp("dateInscription").toInstant(),
                        rs.getBoolean("estBenevole"),
                        rs.getBoolean("estEnLigne")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public static List<Tache> getAllTaches() {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT tache.id as tache_id, tache.description, tache.dateDebut, tache.dateFin, tache.statut, tache.sync_status, "
                + "u.id as user_id, u.nom as user_nom, u.prenom as user_prenom, u.email as user_email, u.motDePasse as user_motDePasse, u.role as user_role, "
                + "u.dateInscription as user_dateInscription, u.estBenevole as user_estBenevole, u.estEnLigne as user_estEnLigne, "
                + "r.id as ressource_id, r.nom as ressource_nom, r.type as ressource_type, r.quantite as ressource_quantite, r.emplacement as ressource_emplacement, r.sync_status as ressource_sync_status "
                + "FROM tache "
                + "INNER JOIN ressource r on r.id = tache.ressourceId "
                + "INNER JOIN user u on u.id = tache.responsableId WHERE tache.sync_status IS NOT 'deleted'";


        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_nom"),
                        rs.getString("user_prenom"),
                        rs.getString("user_email"),
                        rs.getString("user_motDePasse"),
                        rs.getString("user_role"),
                        rs.getTimestamp("user_dateInscription").toInstant(),
                        rs.getBoolean("user_estBenevole"),
                        rs.getBoolean("user_estEnLigne")
                );
                Ressource ressource = new Ressource(
                        rs.getInt("ressource_id"),
                        rs.getString("ressource_nom"),
                        rs.getString("ressource_type"),
                        rs.getInt("ressource_quantite"),
                        rs.getString("ressource_emplacement"),
                        rs.getString("ressource_sync_status")
                );
                Tache tache = new Tache(
                        rs.getInt("tache_id"),
                        rs.getString("description"),
                        rs.getTimestamp("dateDebut").toInstant(),
                        rs.getTimestamp("dateFin").toInstant(),
                        rs.getString("statut"),
                        rs.getString("sync_status")
                );
                tache.setResponsable(user);
                tache.setRessource(ressource);
                taches.add(tache);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return taches;
    }



    public static List<Ressource> getAllRessources() {
        List<Ressource> ressources = new ArrayList<>();
        String sql = "SELECT * FROM ressource WHERE sync_status IS NOT 'deleted'";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ressource ressource = new Ressource(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("emplacement"),
                        SyncStatus.PENDING.getStatus()
                );
                ressources.add(ressource);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ressources;
    }

    public static List<Evenement> getAllEvenement() {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenement";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getTimestamp("date").toInstant(),
                        rs.getString("description"),
                        rs.getString("lieu")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return evenements;
    }

    public static List<AG> getAllAG() {
        List<AG> ags = new ArrayList<>();
        String sql = "SELECT * FROM ag";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AG ag = new AG(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getTimestamp("date").toInstant(),
                        rs.getInt("quorum")
                );
                ags.add(ag);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ags;
    }

    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email like ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("role"),
                        rs.getTimestamp("dateInscription").toInstant(),
                        rs.getBoolean("estBenevole"),
                        rs.getBoolean("estEnLigne")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void saveUser(User user) {
        String sql = "INSERT INTO user (nom, prenom, email, motDePasse, role, dateInscription, estBenevole, estEnLigne) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getMotDePasse());
            pstmt.setString(5, user.getRole());
            pstmt.setTimestamp(6, Timestamp.from(user.getDateInscription()));
            pstmt.setBoolean(7, user.isEstBenevole());
            pstmt.setBoolean(8, user.isEstEnLigne());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createRessource(Ressource ressource) {
        String sql = "INSERT INTO ressource (nom, type, quantite, emplacement, sync_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEmplacement());
            pstmt.setString(5, SyncStatus.NEW.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateRessource(Ressource ressource) {
        String sql = "UPDATE ressource SET nom = ?, type = ?, quantite = ?, emplacement = ?, sync_status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setInt(3, ressource.getQuantite());
            pstmt.setString(4, ressource.getEmplacement());
            pstmt.setString(5, SyncStatus.UPDATED.getStatus());
            pstmt.setInt(6, ressource.getRessourceID());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteRessource(int id) {
        String sql = "UPDATE ressource SET sync_status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SyncStatus.DELETED.getStatus());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTache(Tache tache) {
        String sql = "INSERT INTO tache (description, dateDebut, dateFin, statut, responsableId, ressourceId, sync_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getDescription());
            pstmt.setTimestamp(2, Timestamp.from(tache.getDateDebut()));
            pstmt.setTimestamp(3, Timestamp.from(tache.getDateFin()));
            pstmt.setString(4, tache.getStatut());
            pstmt.setInt(5, tache.getResponsableId());
            pstmt.setInt(6, tache.getRessourceId());
            pstmt.setString(7, SyncStatus.NEW.getStatus());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void updateTache(Tache tache) {
        String sql = "UPDATE tache SET description = ?, dateDebut = ?, dateFin = ?, statut = ?, responsableId = ?, ressourceId = ?, sync_status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getDescription());
            pstmt.setTimestamp(2, Timestamp.from(tache.getDateDebut()));
            pstmt.setTimestamp(3, Timestamp.from(tache.getDateFin()));
            pstmt.setString(4, tache.getStatut());
            pstmt.setInt(5, tache.getResponsable().getId());
            pstmt.setInt(6, tache.getRessource().getRessourceID());
            pstmt.setString(7, SyncStatus.UPDATED.getStatus());
            pstmt.setInt(8, tache.getTacheID());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteTache(int id) {
        String sql = "UPDATE tache SET sync_status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SyncStatus.DELETED.getStatus());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateTacheSyncStatus(int tacheID, String syncStatus) {
        String sql = "UPDATE tache SET sync_status = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syncStatus);
            pstmt.setInt(2, tacheID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateRessourceSyncStatus(int ressourceID, String syncStatus) {
        String sql = "UPDATE ressource SET sync_status = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syncStatus);
            pstmt.setInt(2, ressourceID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthodes pour récupérer les enregistrements par sync_status
    public static List<Tache> getTachesBySyncStatus(String syncStatus) {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT id, description, dateDebut,dateFin,statut,responsableId,ressourceId,sync_status FROM tache WHERE sync_status = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syncStatus);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Tache tache = new Tache(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getTimestamp("dateDebut").toInstant(),
                        rs.getTimestamp("dateFin").toInstant(),
                        rs.getString("statut"),
                        rs.getInt("responsableId"),
                        rs.getInt("ressourceId"),
                        rs.getString("sync_status")
                );
                taches.add(tache);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return taches;
    }

    public static List<Ressource> getRessourcesBySyncStatus(String syncStatus) {
        List<Ressource> ressources = new ArrayList<>();
        String sql = "SELECT id, nom,type,quantite,emplacement,sync_status FROM ressource WHERE sync_status = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syncStatus);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ressource ressource = new Ressource(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("emplacement"),
                        rs.getString("sync_status")
                );
                ressources.add(ressource);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ressources;
    }

    public static List<Ressource> getRessourcesBySyncStatusDelete(String syncStatus) {
        List<Ressource> ressources = new ArrayList<>();
        String sql = "SELECT id, nom,type,quantite,emplacement,sync_status FROM ressource WHERE sync_status = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, syncStatus);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ressource ressource = new Ressource(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("emplacement"),
                        rs.getString("sync_status")
                );
                ressources.add(ressource);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ressources;
    }

    // Méthode pour supprimer définitivement une tâche
    public static void deleteTachePermanently(int tacheID) {
        String sql = "DELETE FROM tache WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tacheID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour supprimer définitivement une ressource
    public static void deleteRessourcePermanently(int ressourceID) {
        String sql = "DELETE FROM ressource WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ressourceID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateTables() {
        try (Connection conn = SQLiteHelper.connect();
             Statement stmt = conn.createStatement()) {

            // Vérifiez l'existence de la colonne 'sync_status' dans la table 'ressource'
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(ressource);");
            boolean hasSyncStatusColumnRessource = false;
            while (rs.next()) {
                if ("sync_status".equals(rs.getString("name"))) {
                    hasSyncStatusColumnRessource = true;
                    break;
                }
            }

            // Vérifiez l'existence de la colonne 'sync_status' dans la table 'tache'
            rs = stmt.executeQuery("PRAGMA table_info(tache);");
            boolean hasSyncStatusColumnTache = false;
            while (rs.next()) {
                if ("sync_status".equals(rs.getString("name"))) {
                    hasSyncStatusColumnTache = true;
                    break;
                }
            }

            // Si les colonnes 'sync_status' n'existent pas, recréez les tables
            if (!hasSyncStatusColumnRessource) {
                stmt.execute("ALTER TABLE ressource RENAME TO old_ressource;");
                stmt.execute("CREATE TABLE ressource ("
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " nom TEXT NOT NULL,"
                        + " type TEXT NOT NULL,"
                        + " quantite INTEGER DEFAULT 1,"
                        + " emplacement TEXT DEFAULT NULL,"
                        + " sync_status TEXT DEFAULT 'synced'"
                        + ");");
                stmt.execute("INSERT INTO ressource (id, nom, type, quantite, emplacement) SELECT id, nom, type, quantite, emplacement FROM old_ressource;");
                stmt.execute("DROP TABLE old_ressource;");
            }

            if (!hasSyncStatusColumnTache) {
                stmt.execute("ALTER TABLE tache RENAME TO old_tache;");
                stmt.execute("CREATE TABLE tache ("
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " description TEXT NOT NULL,"
                        + " dateDebut DATE NOT NULL,"
                        + " dateFin DATE NOT NULL,"
                        + " statut TEXT NOT NULL,"
                        + " responsableId INTEGER,"
                        + " ressourceId INTEGER,"
                        + " sync_status TEXT DEFAULT 'synced',"
                        + " FOREIGN KEY (responsableId) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + " FOREIGN KEY (ressourceId) REFERENCES ressource(id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
                stmt.execute("INSERT INTO tache (id, description, dateDebut, dateFin, statut, responsableId, ressourceId) SELECT id, description, dateDebut, dateFin, statut, responsableId, ressourceId FROM old_tache;");
                stmt.execute("DROP TABLE old_tache;");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}


