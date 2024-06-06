package com.ecaf.ecafclientjava.entites;

import java.time.Instant;
import java.time.LocalDate;

public class Evenement {
    private Instant date;
    private String nom;
    private String description;
    private String lieu;

    public Evenement(String nom, Instant date, String description, String lieu) {
        this.nom = nom;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
    }

    public Instant getDate() {
        return date;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getLieu() {
        return lieu;
    }
}
