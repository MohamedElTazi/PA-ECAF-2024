package com.ecaf.ecafclientjava.entites;

public class Ressource {
    private int id;
    private String nom;
    private String type;
    private int quantite;


    private String emplacement;



    public Ressource(int ressourceID, String nom, String type, int quantite, String emplacement) {
        this.id = ressourceID;
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.emplacement = emplacement;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Getters et setters
    public int getRessourceID() {
        return id;
    }

    public void setRessourceID(int ressourceID) {
        this.id = ressourceID;
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



    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    @Override
    public String toString() {
        return "Ressource{" +
                "ressourceID=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                ", emplacement='" + emplacement + '\'' +
                '}';
    }
}
