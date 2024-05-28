package com.ecaf.ecafclientjava.technique;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnexionBDD {


    private static String dbURL = "jdbc:mariadb://localhost:3307/ECAF" ;
    private static String user = "root" ;
    private static String password = "" ;

    private static Connection connexion = null ;

    private ConnexionBDD() throws ConnexionException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connexion = DriverManager.getConnection(dbURL, user, password) ;
        }
        catch(SQLException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }
    public static Connection getConnexion() throws ConnexionException {
        if( connexion == null ){
            new ConnexionBDD() ;
        }
        return connexion ;
    }

}