package com.ecaf.ecafclientjava.entites;


import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private boolean estBenevole;
    private Instant dateInscription;
    private String token;
    private boolean estEnLigne; // Ajoutez cet attribut


    // Constructeur par défaut
    public User() {
    }

    // Constructeur
    public User(int id, String nom, String prenom, String email, String motDePasse, String role, Instant dateInscription, boolean estBenevole, String token, boolean estEnLigne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateInscription = dateInscription;
        this.estBenevole = estBenevole;
        this.token = token;
        this.estEnLigne = estEnLigne;
    }

    public User(int id, String nom, String prenom, String email, String motDePasse, String role, Instant dateInscription, boolean estBenevole, boolean estEnLigne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateInscription = dateInscription;
        this.estBenevole = estBenevole;
        this.estEnLigne = estEnLigne;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEstBenevole() {
        return estBenevole;
    }

    public void setEstBenevole(boolean estBenevole) {
        this.estBenevole = estBenevole;
    }

    public Instant getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Instant dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isEstEnLigne() {
        return estEnLigne;
    }

    public void setEstEnLigne(boolean estEnLigne) {
        this.estEnLigne = estEnLigne;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", role='" + role + '\'' +
                ", estBenevole=" + estBenevole +
                ", dateInscription=" + dateInscription +
                ", token='" + token + '\'' +
                ", estEnLigne=" + estEnLigne +
                '}';
    }
}