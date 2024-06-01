package com.ecaf.ecafclientjava.technique;

import com.ecaf.ecafclientjava.entites.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    @SerializedName("Users")

    private List<User> users; // Correction du nom de la propriété
    private int totalCount;

    // Getters et setters
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
