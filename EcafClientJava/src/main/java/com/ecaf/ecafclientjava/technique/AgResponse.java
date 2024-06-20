package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.AG;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;



@JsonIgnoreProperties(ignoreUnknown = true)
public class AgResponse {
    @SerializedName("Ags")

    private List<AG> ags;
    private int totalCount;


    public List<AG> getAgs() {
        return ags;
    }

    public void setAgs(List<AG> ags) {
        this.ags = ags;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
