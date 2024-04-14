package org.example;
import java.util.*;

public class Evenements {
    public Integer eventId;
    public String nom;
    public Date date;
    public String description;
    public String lieu;

    public Evenements(Integer eventId, String nom, Date date, String description, String lieu) {
        this.eventId = eventId;
        this.nom = nom;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
    }
}
