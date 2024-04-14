package org.example;
package fr.gsb.rv.technique;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnexionBDD {

    private static String dbURL = "ECAF:mysql://localhost:3307/gsbrv" ;
    private static String user = "ecafUser" ;
    private static String password = "azerty" ;

    private static Connection connexion = null ;

    private ConnexionBDD() throws ConnexionException {
        try {
            Class.forName( "com.mysql.jdbc.Driver" ) ;
            connexion = (Connection) DriverManager.getConnection(dbURL, user, password) ;
        }
        catch( Exception e ){
            throw new ConnexionException() ;
        }
    }

    public static Connection getConnexion() throws ConnexionException {
        if( connexion == null ){
            new ConnexionBDD() ;
        }
        return connexion ;
    }
}