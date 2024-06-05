package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.Tache;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TacheResponse {
    @SerializedName("Taches")

    private List<Tache> taches; // Correction du nom de la propriété
    private int totalCount;

    // Getters et setters
    public List<Tache> getTaches() {
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
