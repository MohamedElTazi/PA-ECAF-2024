package org.example;
import java.util.*;
public class Ressources {
    public Integer ressourceId;
    public String nom;
    public String type;
    public String statut;
    public String emplacement;

    public Ressources(Integer ressourceId, String nom, String type, String statut, String emplacement) {
        this.ressourceId = ressourceId;
        this.nom = nom;
        this.type = type;
        this.statut = statut;
        this.emplacement = emplacement;
    }
}
