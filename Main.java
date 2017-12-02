package ua.i.licit;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartmentDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of apartments to be created:");
        int x = sc.nextInt();
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
            try (Statement st = conn.createStatement()) {
                createTable(st);
                insertData(conn, x);
                viewResults(st);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Statement st) {
        try {
            st.execute("DROP TABLE IF EXISTS apartments");
            st.execute("CREATE TABLE apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(30) NOT NULL, address VARCHAR (30) NOT NULL, area INT NOT NULL," +
                    "room INT NOT NULL, price DOUBLE DEFAULT NULL )");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertData(Connection conn, int x) {
        Random rn = new Random();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO apartments (district, address, area, room, price) " +
                    "VALUES('district','address',?,?,?)")) {
                for (int i = 0; i < x; i++) {
                    ps.setDouble(1, 1 + rn.nextInt(100));
                    ps.setInt(2, rn.nextInt(4) + 1);
                    ps.setDouble(3, 10000 * rn.nextDouble());
                    ps.executeUpdate();
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewResults(Statement st) {
        try (ResultSet rs = st.executeQuery("SELECT * FROM apartments")) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
