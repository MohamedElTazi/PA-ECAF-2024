package com.ecaf.ecafclientjava.entites;

import java.time.Instant;
import java.time.LocalDate;

public class Tache {
    private int id;
    private String description;
    private Instant dateDebut;
    private Instant dateFin;
    private String statut;

    // Associations
    private User responsable;

    // Constructeur
    public Tache(int tacheID, String description, Instant dateDebut, Instant dateFin, String statut, User responsable) {
        this.id = tacheID;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.responsable = responsable;
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

    @Override
    public String toString() {
        return "Tache{" +
                "tacheID=" + id +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", responsable=" + responsable +
                '}';
    }


}
