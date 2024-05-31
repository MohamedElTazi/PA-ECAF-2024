package com.ecaf.ecafclientjava.entites;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


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

    // Associations
    private List<Reservation> reservations;
    private List<Tache> tachesResponsable;



    // Constructeur
    public User(int userID, String nom, String prenom, String email, String motDePasse, String role,  Instant dateInscription ,boolean estBenevole, String token) {
        this.id = userID;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.estBenevole = estBenevole;
        this.dateInscription = dateInscription;
        this.token = token;
    }

    // Getters et setters
    public int getUserID() {
        return id;
    }

    public void setUserID(int userID) {
        this.id = userID;
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Tache> getTachesResponsable() {
        return tachesResponsable;
    }

    public void setTachesResponsable(List<Tache> tachesResponsable) {
        this.tachesResponsable = tachesResponsable;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", role='" + role + '\'' +
                ", estBenevole=" + estBenevole +
                ", dateInscription=" + dateInscription +
                ", token='" + token + '\'' +
                ", reservations=" + reservations +
                ", tachesResponsable=" + tachesResponsable +
                '}';
    }
}
