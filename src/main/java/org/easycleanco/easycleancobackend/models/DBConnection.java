package org.easycleanco.easycleancobackend.models;


import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        String url = "jdbc:postgresql://ep-empty-hall-a1kqf4ue-pooler.ap-southeast-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_5VAiGfKphjg1&sslmode=require";
        String user = "neondb_owner";
        String password = "npg_5VAiGfKphjg1";
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
