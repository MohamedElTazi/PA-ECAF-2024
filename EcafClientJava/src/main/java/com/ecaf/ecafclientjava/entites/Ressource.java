package com.ecaf.ecafclientjava.entites;

public class Ressource {
    private int ressourceID;
    private String nom;
    private String type;
    private String statut;



    private String emplacement;

    // Constructeur
    public Ressource(int ressourceID, String nom, String type, String statut, String emplacement) {
        this.ressourceID = ressourceID;
        this.nom = nom;
        this.type = type;
        this.statut = statut;
        this.emplacement = emplacement;
    }

    // Getters et setters
    public int getRessourceID() {
        return ressourceID;
    }

    public void setRessourceID(int ressourceID) {
        this.ressourceID = ressourceID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    @Override
    public String toString() {
        return "Ressource{" +
                "ressourceID=" + ressourceID +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", statut='" + statut + '\'' +
                ", emplacement='" + emplacement + '\'' +
                '}';
    }
}
