// src/main/java/util/DBConnect.java
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
private static final String URL = "jdbc:mysql://localhost:3306/may_diet_co?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh";

    private static final String USER = "root";
    private static final String PASS = "root"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
