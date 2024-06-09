package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.Evenement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {
    @SerializedName("Evenements")

    private List<Evenement> evenements; // Correction du nom de la propriété
    private int totalCount;

    // Getters et setters
    public List<Evenement> getEvent() {
        return evenements;
    }

    public void setEvent(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}