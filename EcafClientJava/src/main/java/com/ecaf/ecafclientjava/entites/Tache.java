package com.ecaf.ecafclientjava.entites;

import java.time.Instant;
import java.time.LocalDate;

public class Tache {
    private int id;
    private String description;
    private Instant dateDebut;
    private Instant dateFin;
    private String statut;
    private String sync_status;


    // Associations
    private User responsable;
    private Ressource ressource;

    private int responsableId;
    private int ressourceId;

    // Constructeur
    public Tache(int tacheID, String description, Instant dateDebut, Instant dateFin, String statut, User responsable,Ressource ressource,String sync_status) {
        this.id = tacheID;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.responsable = responsable;
        this.ressource = ressource;
        this.sync_status = sync_status;;
    }

    public Tache(int tacheID, String description, Instant dateDebut, Instant dateFin, String statut, int responsableId, int ressourceId, String sync_status) {
        this.id = tacheID;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.responsableId = responsableId;
        this.ressourceId = ressourceId;
        this.sync_status = sync_status;;
    }

    public Tache(String description, Instant dateDebut, Instant dateFin, String statut, int responsableId, int ressourceId, String sync_status) {
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.responsableId = responsableId;
        this.ressourceId = ressourceId;
        this.sync_status = sync_status;;
    }

    public int getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId;
    }

    public int getRessourceId() {
        return ressourceId;
    }

    public void setRessourceId(int ressourceId) {
        this.ressourceId = ressourceId;
    }

    public Tache(int id, String description, Instant dateDebut, Instant dateFin, String statut, String sync_status) {
        this.id = id;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.sync_status = sync_status;;

    }

    // Getters et setters
    public int getTacheID() {
        return id;
    }

    public void setTacheID(int tacheID) {
        this.id = tacheID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFin() {
        return dateFin;
    }

    public void setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public User getResponsable() {
        return responsable;
    }

    public void setResponsable(User responsable) {
        this.responsable = responsable;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }


    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", responsable=" + responsable +
                ", tache=" + ressource +
                '}';
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }
}
