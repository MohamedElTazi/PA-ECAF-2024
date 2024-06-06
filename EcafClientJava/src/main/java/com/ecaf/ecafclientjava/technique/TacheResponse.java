package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.Tache;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TacheResponse {
    @SerializedName("Taches")

    private List<Tache> taches; // Correction du nom de la propriété
    private int totalCount;

    // Getters et setters
    public List<Tache> getTache() {
        return taches;
    }

    public void setTache(List<Tache> taches) {
        this.taches = taches;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
