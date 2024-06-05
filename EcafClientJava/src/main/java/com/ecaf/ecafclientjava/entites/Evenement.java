package com.ecaf.ecafclientjava.entites;

import java.time.LocalDate;

public class Evenement {
    private LocalDate date;
    private String titre;
    private String description;
    private String lieu;

    public Evenement(String titre, LocalDate date, String description, String lieu) {
        this.titre = titre;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getLieu() {
        return lieu;
    }
}
