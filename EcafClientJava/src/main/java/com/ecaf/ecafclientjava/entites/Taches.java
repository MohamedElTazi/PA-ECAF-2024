package com.ecaf.ecafclientjava.entites;

import java.util.Date;

public class Taches {
    public Integer tacheId;
    public String description;
    public Date dateDeb;
    public Date dateFin;
    public String statut;

    public Taches(Integer tacheId, String description, Date dateDeb, Date dateFin, String statut) {
        this.tacheId = tacheId;
        this.description = description;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.statut = statut;
    }
}
