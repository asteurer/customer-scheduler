package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class simplifies interacting with the appointments table in the database.
 */
public class AppointmentDB {

    /**
     * Converts OS default date time value to UTC.
     */
    private static LocalDateTime convertToUTCTime(LocalDateTime localDateTimeValue) {
        ZonedDateTime localZonedDateTime = localDateTimeValue.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        return utcZonedDateTime.toLocalDateTime();
    }

    /**
     * Converts UTC to OS default date time.
     */
    private static LocalDateTime convertToLocalTime(LocalDateTime utcDateTimeValue) {
        ZonedDateTime utcZonedDateTime = utcDateTimeValue.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());

        return localZonedDateTime.toLocalDateTime();
    }

    /**
     * @return Returns all appointments from the database
     */
    public static ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("SELECT * from appointments");

            while(rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                LocalDateTime start = convertToLocalTime(rs.getTimestamp("Start").toLocalDateTime());
                LocalDateTime end = convertToLocalTime(rs.getTimestamp("End").toLocalDateTime());
                int contactID = rs.getInt("Contact_ID");
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");

                appointmentList.add(new Appointment(id, title, description, location, type, start, end, customerID, userID, contactID));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return appointmentList;
    }

    /**
     * This adds an appointment to the database.
     */
    public static void addAppointment(String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int contactID) {
        try {
            String sqlStatement = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String currentUser = Utilities.getCurrentUser();
            ObservableList<Object> params = FXCollections.observableArrayList(
                    title,
                    description,
                    location,
                    type,
                    convertToUTCTime(start),
                    convertToUTCTime(end),
                    convertToUTCTime(LocalDateTime.now()),
                    currentUser,
                    customerID,
                    Utilities.getUserID(currentUser),
                    contactID
            );

            JDBC.sendUpdate(sqlStatement, params);

        } catch (Exception e) {
            System.out.println("Failed to add appointment: " + e.getMessage());
        }
    }

    /**
     * This deletes an appointment from the database
     */
    public static void deleteAppointment(int appointmentID) {
        try {
            String sqlStatement = "DELETE FROM appointments where Appointment_ID = ?";
            ObservableList<Object> params = FXCollections.observableArrayList(appointmentID);
            JDBC.sendUpdate(sqlStatement, params);
        } catch (Exception e) {
            System.out.println("Failed to delete appointment: " + e.getMessage());
        }
    }

    /**
     * This is an overloaded version of deleteAppointment that deletes based on Customer_ID, rather than Appointment_ID
     */
    public static void deleteAppointment(int customerID, boolean isCustomer) {
        try {
            String sqlStatement = "DELETE FROM appointments where Customer_ID = ?";
            ObservableList<Object> params = FXCollections.observableArrayList(customerID);
            JDBC.sendUpdate(sqlStatement, params);
        } catch (Exception e) {
            System.out.println("Failed to delete appointment: " + e.getMessage());
        }
    }

    /**
     * This modifies an appointment in the database.
     */
    public static void modifyAppointment(
            int appointmentID,
            String title,
            String description,
            String location,
            String type,
            LocalDateTime start,
            LocalDateTime end,
            int customerID,
            int contactID
    ) {
        try{
            String sqlStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
            String currentUser = Utilities.getCurrentUser();
            ObservableList<Object> params = FXCollections.observableArrayList(
                    title,
                    description,
                    location,
                    type,
                    convertToUTCTime(start),
                    convertToUTCTime(end),
                    currentUser,
                    customerID,
                    Utilities.getUserID(currentUser),
                    contactID,
                    appointmentID
            );

            JDBC.sendUpdate(sqlStatement, params);

        } catch (Exception e) {
            System.out.println("Failed to modify appointment: " + e.getMessage());
        }
    }
}
