package com.ecaf.ecafclientjava.entites;

import java.time.Instant;
import java.util.Date;

public class Evenement {
    public Integer id;
    public String nom;
    public Instant date;
    public String description;
    public String lieu;

    public Evenement(Integer eventId, String nom, Instant date, String description, String lieu) {
        this.id = eventId;
        this.nom = nom;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
    }

    public Integer getEventId() {
        return id;
    }

    public void setEventId(Integer eventId) {
        this.id = eventId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "eventId=" + id +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
