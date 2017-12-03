package ua.i.licit;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartmentDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";


    public static void main(String[] args) {
        System.out.print("Enter the number of apartments to be created:");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
             Statement st = conn.createStatement(); Scanner sc = new Scanner(System.in)) {
            int x = sc.nextInt();
            createTable(st);
            insertData(conn, x);
            viewResults(st);
            System.out.println();
            selectParameters(conn, st, sc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Statement st) {
        try {
            st.execute("DROP TABLE IF EXISTS apartments");
            st.execute("CREATE TABLE apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(30) NOT NULL, address VARCHAR (30) NOT NULL, area DOUBLE(10,6) DEFAULT NULL," +
                    "room INT DEFAULT NULL, price DECIMAL(10,2) DEFAULT NULL )");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertData(Connection conn, int x) {
        Random rn = new Random();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO apartments (district, address, area, room, price) " +
                    "VALUES(?,?,?,?,?)")) {
                for (int i = 0; i < x; i++) {
                    ps.setString(1, "district " + (i + 1));
                    ps.setString(2, "address " + (i + 1));
                    ps.setDouble(3, 100 * rn.nextDouble());
                    ps.setInt(4, rn.nextInt(5) + 1);
                    ps.setDouble(5, 100000 * rn.nextDouble());
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

    private static void selectParameters(Connection conn, Statement st, Scanner sc) {
        while (true) {
            System.out.println("1: select an apartment by district");
            System.out.println("2: select an apartment by address");
            System.out.println("3: select an apartment by area");
            System.out.println("4: select an apartment by room");
            System.out.println("5: select an apartment by price");
            System.out.println("6: view all the apartments");
            System.out.print("-> ");
            sc = new Scanner(System.in);
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    selectByDistrict(conn, sc);
                    System.out.println();
                    break;
                case "2":
                    selectByAddress(conn, sc);
                    System.out.println();
                    break;
                case "3":
                    selectByArea(conn, sc);
                    System.out.println();
                    break;
                case "4":
                    selectByRoom(conn, sc);
                    System.out.println();
                    break;
                case "5":
                    selectByPrice(conn, sc);
                    System.out.println();
                    break;
                case "6":
                    viewResults(st);
                    System.out.println();
                    break;
                default:
                    return;
            }
        }
    }

    private static void selectByDistrict(Connection conn, Scanner sc) {
        System.out.println("Enter a district: ");
        sc = new Scanner(System.in);
        String text = sc.nextLine();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments WHERE district=?")) {
            ps.setString(1, text);
            ps.execute();
            viewResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectByAddress(Connection conn, Scanner sc) {
        System.out.println("Enter an address: ");
        sc = new Scanner(System.in);
        String text = sc.nextLine();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments WHERE address=?")) {
            ps.setString(1, text);
            ps.execute();
            viewResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectByArea(Connection conn, Scanner sc) {
        System.out.println("Enter an area: ");
        sc = new Scanner(System.in);
        double area = sc.nextDouble();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments WHERE area=?")) {
            ps.setDouble(1, area);
            ps.execute();
            viewResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectByRoom(Connection conn, Scanner sc) {
        System.out.println("Enter an amount of rooms: ");
        sc = new Scanner(System.in);
        int rooms = sc.nextInt();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments WHERE room=?")) {
            ps.setInt(1, rooms);
            ps.execute();
            viewResultSet(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectByPrice(Connection conn, Scanner sc) {
        System.out.println("Enter a price: ");
        sc = new Scanner(System.in);
        String text = sc.nextLine();
        BigDecimal price = new BigDecimal(text);
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM apartments WHERE price=?")) {
            ps.setBigDecimal(1, price);
            ps.execute();
            viewResultSet(ps);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static void viewResultSet(PreparedStatement ps) {
        try (ResultSet rs = ps.getResultSet()) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewResults(Statement st) {
        try (ResultSet rs = st.executeQuery("SELECT * FROM apartments")) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
