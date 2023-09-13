package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import steurer.software_two.model.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class simplifies interacting with the contacts table in the database.
 */
public class ContactDB {
    /**
     * @return This returns all contacts from the database
     */
    public static ObservableList<Contact> getAllContacts(){
        ObservableList<Contact> contactList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("SELECT * from contacts");

            while(rs.next()) {
                int contactID = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String contactEmail = rs.getString("Email");

                contactList.add(new Contact(contactID, contactName, contactEmail));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contactList;
    }
}
