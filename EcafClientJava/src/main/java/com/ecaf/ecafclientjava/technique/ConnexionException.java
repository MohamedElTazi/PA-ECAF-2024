package com.ecaf.ecafclientjava.technique;

public class ConnexionException extends Exception {
    @Override
    public String getMessage(){
        return "[Nok] Connexion BD" ;
    }
}
