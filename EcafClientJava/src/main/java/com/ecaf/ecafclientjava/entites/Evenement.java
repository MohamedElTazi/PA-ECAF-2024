package com.ecaf.ecafclientjava.entites;

import java.time.LocalDate;

public class Evenement {
    private final LocalDate date;
    private final String description;

    public Evenement(LocalDate date, String description) {
        this.date = date;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
