package com.ecaf.ecafclientjava.entites;

import java.time.LocalDate;

public class Tache {
    private int tacheID;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;

    // Associations
    private User responsable;

    // Constructeur
    public Tache(int tacheID, String description, LocalDate dateDebut, LocalDate dateFin, String statut, User responsable) {
        this.tacheID = tacheID;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.responsable = responsable;
    }

    // Getters et setters
    public int getTacheID() {
        return tacheID;
    }

    public void setTacheID(int tacheID) {
        this.tacheID = tacheID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
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
                "tacheID=" + tacheID +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", responsable=" + responsable +
                '}';
    }
}
