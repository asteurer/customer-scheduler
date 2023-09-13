package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import steurer.software_two.model.Customer;
import steurer.software_two.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class simplifies interacting with the users table in the database.
 */
public class UserDB {
    public static ObservableList<User> getAllUsers(){
        ObservableList<User> userList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("SELECT * from users");

            while(rs.next()) {
                int id = rs.getInt("User_ID");
                String username = rs.getString("User_Name");
                String password = rs.getString("Password");

                userList.add(new User(id, username, password));
            }

        } catch (SQLException e) {
        System.out.println("failed");
            throw new RuntimeException(e);
        }

        return userList;
    }
}
