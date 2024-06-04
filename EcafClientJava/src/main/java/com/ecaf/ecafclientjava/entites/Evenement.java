package com.ecaf.ecafclientjava.entites;

import java.time.LocalDate;

public class Evenement {
    private LocalDate date;
    private String titre;
    private String description;
    private String lieu;

    public Evenement(LocalDate date, String titre, String description, String lieu) {
        this.date = date;
        this.titre = titre;
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
