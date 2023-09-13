package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Contact;
import steurer.software_two.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * This class simplifies interacting with the customers table in the database.
 */
public class CustomerDB {
    /**
     * @return Returns all customers from the database.
     */
    public static ObservableList<Customer> getAllCustomers(){
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("SELECT * FROM customers");

            while(rs.next()) {
                int id = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String zip = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                String division = Utilities.getDivisionName(divisionID);
                String country = Utilities.getCountryFromDivision(divisionID);

                customerList.add(new Customer(id, name, address, zip, phone, country, division));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return customerList;
    }

    /**
     * This adds a customer to the database.
     */
    public static void addCustomer(String name, String address, String zip, String phone, String division) {
        int divisionID = Utilities.getDivisionID(division);
        String currentUser = Utilities.getCurrentUser();

        try {
            String sqlStatement = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Division_ID) VALUES(?, ?, ?, ?, ?, ?, ?)";
            ObservableList<Object> params = FXCollections.observableArrayList(name, address, zip, phone, LocalDateTime.now(), currentUser, divisionID);
            JDBC.sendUpdate(sqlStatement, params);
        } catch (Exception e) {
            System.out.println("Failed to add customer: " + e.getMessage());
        }
    }

    /**
     * This deletes a customer from the database
     */
    public static void deleteCustomer(int id){
        try {
            String sqlStatement = "DELETE FROM customers WHERE Customer_ID = ?";
            ObservableList<Object> params = FXCollections.observableArrayList(id);
            JDBC.sendUpdate(sqlStatement, params);
        } catch (Exception e) {
            System.out.println("Failed to delete customer: " + e.getMessage());
        }
    }

    /**
     * This modifies a customer in the database.
     */
    public static void modifyCustomer(Customer customer) {
        try {
            String sqlStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
            ObservableList<Object> params = FXCollections.observableArrayList(
                    customer.getName(),
                    customer.getAddress(),
                    customer.getZip(),
                    customer.getPhone(),
                    LocalDateTime.now(),
                    Utilities.getCurrentUser(),
                    Utilities.getDivisionID(customer.getDivision()),
                    customer.getId()
            );
            JDBC.sendUpdate(sqlStatement, params);
        } catch (Exception e) {
            System.out.println("Failed to modify customer: " + e.getMessage());
        }
    }
}
