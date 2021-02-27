package com.legacy.asset.move;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionDB {

    private static final Dotenv dotenv = Dotenv.load();

    public Connection con;
    public Statement st;

    public ConnectionDB() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            con = DriverManager.getConnection(dotenv.get("DB_URI"));
            st = con.createStatement();
            System.out.println("Connection is successful");
        } catch (Exception e) {
            System.out.println("Connection failed...");
            System.out.println(e.getMessage());
        }
    }

}
