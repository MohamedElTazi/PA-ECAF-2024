package com.ecaf.ecafclientjava.technique;


import com.ecaf.ecafclientjava.entites.User;

public class Session {
   static private Session session = null;
   private User ladministrateur ;

    private Session(User ladministrateur) {
        this.ladministrateur = ladministrateur;
    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        Session.session = session;
    }

    public User getLeVisiteur() {
        return ladministrateur;
    }

    public void setLeVisiteur(User ladministrateur) {
        this.ladministrateur = ladministrateur;
    }

    static public void ouvrir(User ladministrateur){
        Session.session = new Session(ladministrateur);
    }

    static public void fermer(){
        Session.session = null;
    }

    static public boolean estOuverte(){
        if(Session.session != null){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Session{" +
                "leVisiteur=" + ladministrateur +
                "session=" + session +
                '}';
    }
}

