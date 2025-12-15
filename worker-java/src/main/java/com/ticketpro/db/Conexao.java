package com.ticketpro.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection getConexao() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");

        return DriverManager.getConnection(url, user, password);
    }
}
