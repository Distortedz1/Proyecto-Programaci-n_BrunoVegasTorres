package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3309/cine_granada?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "admin";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
