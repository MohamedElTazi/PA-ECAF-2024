package com.ecaf.ecafclientjava.entites;

import java.util.Date;

public class Evenement {
    public Integer eventId;
    public String nom;



    public Date date;
    public String description;
    public String lieu;

    public Evenement(Integer eventId, String nom, Date date, String description, String lieu) {
        this.eventId = eventId;
        this.nom = nom;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
                "eventId=" + eventId +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
