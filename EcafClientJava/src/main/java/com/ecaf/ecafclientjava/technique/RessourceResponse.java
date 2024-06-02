package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.Ressource;
import com.ecaf.ecafclientjava.entites.Tache;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RessourceResponse {
    @SerializedName("Ressources")

    private List<Ressource> ressources; // Correction du nom de la propriété
    private int totalCount;

    // Getters et setters
    public List<Ressource> getTaches() {
        return ressources;
    }

    public void setTaches(List<Ressource> taches) {
        this.ressources = taches;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
