package steurer.software_two.DAO;

import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * This is the class that interacts directly with the database. Any SQL statements that allow for variables have been converted into prepared statements to protect against SQL injection.
 */
public class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    // This was "//localhost/"
    private static final String location = "//wgu-software-two.cppfrnv5lgkv.us-east-1.rds.amazonaws.com/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcURL = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // local
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "admin";
    private static final String password = "Passw0rd!";
    private static Connection connection;

    public static void openConnection() {
        try {
            Class.forName(driver); // locate driver
            connection = DriverManager.getConnection(jdbcURL, userName, password); // reference connection object
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    /**
     *
     * @return Returns the data requested from the database.
     */
    public static ResultSet sendQuery(String sqlStatement) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sqlStatement);
            return ps.executeQuery();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This sends an update request to the database.
     * @param params This is an array of objects that contain the parameters specified in the sqlStatement.
     */
    public static void sendUpdate(String sqlStatement, ObservableList<Object> params) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sqlStatement);

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                // Protecting against SQL injection
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof LocalDateTime) {
                    ps.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) param));
                } else {
                    // If the type is not handled...
                    throw new IllegalArgumentException("Unhandled parameter type: " + param.getClass().getName());
                }
            }
            ps.executeUpdate();
            System.out.println("Update Successful");
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
