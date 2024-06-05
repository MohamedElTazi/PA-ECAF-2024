package com.ecaf.ecafclientjava.entites;

import java.time.LocalDate;

public class Reservation {
    private int reservationID;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;

    // Association
    private Ressource ressource;



    private User utilisateur;

    // Constructeur
    public Reservation(int reservationID, LocalDate dateDebut, LocalDate dateFin, String description, Ressource ressource, User utilisateur) {
        this.reservationID = reservationID;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.ressource = ressource;
        this.utilisateur = utilisateur;
    }

    // Getters et setters
    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationID=" + reservationID +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", description='" + description + '\'' +
                ", ressource=" + ressource +
                ", utilisateur=" + utilisateur +
                '}';
    }
}