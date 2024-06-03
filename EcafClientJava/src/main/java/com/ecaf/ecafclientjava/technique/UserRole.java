package com.ecaf.ecafclientjava.technique;

public enum UserRole {
    VISITEUR("Visiteur"),
    ADMINISTRATEUR("Administrateur"),
    ADHERENT("Adherent");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
