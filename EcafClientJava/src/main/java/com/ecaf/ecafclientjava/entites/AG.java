package com.ecaf.ecafclientjava.entites;

import java.time.Instant;

public class AG {
    private Integer id;
    private String nom;
    private Instant date;
    private String description;
    private String type;
    private Integer quorum;

    public AG(Integer AgId, String nom, String description, String type, Instant date, Integer quorum){
        this.id = AgId;
        this.nom = nom;
        this.date = date;
        this.description = description;
        this.type = type;
        this.quorum = quorum;
    }



    public Integer getAgId() {
        return id;
    }

    public void setAgId(Integer agId) {
        id = agId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public void setQuorum(Integer quorum) {
        this.quorum = quorum;
    }
}
