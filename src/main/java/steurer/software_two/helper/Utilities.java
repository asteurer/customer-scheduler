package steurer.software_two.helper;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import steurer.software_two.DAO.*;
import steurer.software_two.model.*;

import java.io.IOException;
import java.time.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This is a helper class that stores instance variables that couldn't be stored in the actual FXML view, and methods that were reused across views.
 */
public abstract class Utilities {
    private static String currentUser;
    private static int currentAppointmentID;

    public static String getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(String username) {
        currentUser = username;
    }
    public static int getCurrentAppointmentID() {return currentAppointmentID; }
    public static void setCurrentAppointmentID(int appointmentID) {currentAppointmentID = appointmentID; }

    /**
     * This overrides the close request to close the application and redirects the user to the MainForm.
     * LAMBDA EXPRESSION: In order to prevent the application from closing when the exit button is clicked, this uses a lambda expression to consume the event (i.e. the button being closed) and redirect the view to the desired FXML form.
     * @param stage current stage
     */
    //This is the actual mechanism that redirects the scene on close
    private static void onCloseShowForm(Stage stage, String onCloseRedirectFileName) {
        stage.setOnCloseRequest(event -> {
            try {
                Parent newSceneRoot = FXMLLoader.load(Objects.requireNonNull(Utilities.class.getResource(String.format("/steurer/software_two/view/%s.fxml", onCloseRedirectFileName))));
                Scene newScene = new Scene(newSceneRoot);
                stage.setOnCloseRequest(e -> Platform.exit()); // Reset the close button to exit the application once returning to the main menu.
                stage.setScene(newScene);
                stage.show();  // Ensure the stage is shown again.
                event.consume();
            } catch (IOException e) {
                System.out.println(e.getMessage());;
            }
        });
    }

    /**
     * This gets and sets the FXML loader to be used with setStage after data is passed from one scene to another.
     * @param fxmlFileName the filename for the scene to be loaded
     * @return returns the loader
     * @throws IOException throws exception if the loader fails to load
     */
    public static FXMLLoader getLoader(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Utilities.class.getResource(String.format("/steurer/software_two/view/%s.fxml", fxmlFileName)));
        loader.load();
        return loader;
    }

    /**
     * This takes the loader from getLoader, overrides the close request, and shows the new stage and scene.
     * @param loader the loader generated from getLoader
     */

    public static void setStage(FXMLLoader loader, ActionEvent event, String onCloseRedirectFileName, boolean isMainMenu) throws IOException {
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (!isMainMenu) {
            onCloseShowForm(currentStage, onCloseRedirectFileName); //Overriding the close request
        } else {
            currentStage.setOnCloseRequest(e -> Platform.exit());
        }
        Parent scene = loader.getRoot();
        currentStage.setScene(new Scene(scene));
        currentStage.show();
    }

    /**
     * This is designed for the login screen to take any event from any node type and set the stage. This allows someone to hit enter or click a button to proceed to the next screen.
     * @param loader the loader generated from getLoader
     * @param isLogin overloads the previous setStage method
     */

    public static void setStage(FXMLLoader loader, ActionEvent event, boolean isLogin) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        currentStage.setScene(new Scene(scene));
        currentStage.show();
    }

    /**
     * This helps reduce the generation of alerts to one line of code.
     * @param alertType the alert type
     * @param message the bundle label key that corresponds to the message to be displayed
     */
    public static void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.show();
    }

    public static String getContactName(int contactID) {
        ObservableList<Contact> allContacts = ContactDB.getAllContacts();
        String contactName = null;
        for (Contact entry: allContacts) {
            if (entry.getId() == contactID) {
                contactName = entry.getName();
            }
        }

        return contactName;
    }

    /**
     * @param contactName the contact name that needs to be matched to an ID
     * @return if found, returns an integer, else returns -1
     */
    public static int getContactID(String contactName) {
        ObservableList<Contact> allContacts = ContactDB.getAllContacts();
        int contactID = -1;
        for (Contact entry: allContacts) {
            if (Objects.equals(entry.getName(), contactName)) {
                contactID = entry.getId();
            }
        }

        return contactID;
    }

    /**
     * @param userID the user ID that needs to be matched to a username
     * @return if found returns the username string, else returns null
     */
    public static String getUsername(int userID) {
        ObservableList<User> allUsers = UserDB.getAllUsers();
        String username = null;
        for (User entry: allUsers) {
            if (entry.getId() == userID){
                username = entry.getUsername();
            }
        }
        return username;
    }

    /**
     * @param username the username that is to be matched with a user ID
     * @return returns the user id if found; otherwise, returns -1
     */
    public static int getUserID(String username) {
        ObservableList<User> allUsers = UserDB.getAllUsers();
        int userID = -1;
        for (User entry: allUsers) {
            if (Objects.equals(entry.getUsername(), username)) {
                userID = entry.getId();
            }
        }
        return userID ;
    }

    /**
     * This uses the languageBundles resource bundle to translate text based upon the computer's locale.
     * @param bundleLabel The key string for the desired value
     * @return Returns the string in the resource bundle that corresponds to the bundleLabel.
     */
    public static String translate(String bundleLabel) {
        Locale locale = Locale.getDefault();
        ResourceBundle resource = ResourceBundle.getBundle("steurer.software_two.languageBundles.Bundle", locale);

        return resource.getString(bundleLabel);
    }

    /**
     * @param divisionID The id of the desired division
     * @return returns the name associated with the division id, else returns null
     */
    public static String getDivisionName(int divisionID) {
        ObservableList<FirstLevelDivision> allDivisions = FirstLevelDivisionDB.getAllDivisions();
        for (FirstLevelDivision entry: allDivisions) {
            if (divisionID == entry.getDivisionID()) {
                return entry.getDivision();
            }
        }
        return null;
    }

    public static int getDivisionID(String divisionName) {
        ObservableList<FirstLevelDivision> allDivisions = FirstLevelDivisionDB.getAllDivisions();
        for (FirstLevelDivision entry: allDivisions) {
            if (Objects.equals(divisionName, entry.getDivision())) {
                return entry.getDivisionID();
            }
        }
        return 0;
    }

    /**
     * @param divisionID The division ID for the division within the desired country
     * @return The name of the country, else returns null
     */
    public static String getCountryFromDivision(int divisionID) {
        ObservableList<Country> allCountries = CountryDB.getAllCountries();
        ObservableList<FirstLevelDivision> allDivisions = FirstLevelDivisionDB.getAllDivisions();

        for(FirstLevelDivision division: allDivisions) {
            if (division.getDivisionID() == divisionID) {
                for (Country country: allCountries) {
                    if (division.getCountryID() == country.getId()) {
                        return country.getName();
                    }
                }
            }
        }

        return null;
    }


    /**
     * @param customerID The desired customer ID
     * @return Returns the name corresponding with the customer ID.
     */
    public static String getCustomerName(int customerID) {
        ObservableList<Customer> allCustomers = CustomerDB.getAllCustomers();
        for (Customer entry: allCustomers) {
            if (entry.getId() == customerID) {
                return entry.getName();
            }
        }
        return null;
    }

    /**
     * @param customerName The desired customer name
     * @return Returns the customer ID corresponding with the customer name.
     */
    public static int getCustomerID(String customerName) {
        ObservableList<Customer> allCustomers = CustomerDB.getAllCustomers();
        for (Customer entry: allCustomers) {
            if (Objects.equals(entry.getName(), customerName)) {
                return entry.getId();
            }
        }

        return 0;
    }

    /**
     * This gives a confirmation alert and redirects the app to the MainMenuForm on confirmation
     */
    public static void cancelButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Utilities.translate("generalConfirmation"));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            FXMLLoader loader = getLoader("MainMenuForm");
            setStage(loader, event, "MainMenuForm", true);
        }
    }
}
