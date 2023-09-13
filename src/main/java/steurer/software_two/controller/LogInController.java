package steurer.software_two.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import steurer.software_two.DAO.AppointmentDB;
import steurer.software_two.DAO.UserDB;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Appointment;
import steurer.software_two.model.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;



public class LogInController implements Initializable {

    @FXML
    private Label logInTitleLbl;
    @FXML
    private Label usernameFieldLbl;
    @FXML
    private Label passwordFieldLbl;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button goBtn;
    @FXML
    private Label locationLbl;

    /**
     * This writes to a TXT file details about login attempts.
     * @param username The username that was attempted.
     * @param successfulLogin Whether the login was successful.
     */
    private void writeUserDataToFile(String username, boolean successfulLogin) {
        String filename = "login_activity.txt";
        String date = LocalDate.now().toString();
        String time = LocalTime.now().toString();
        String success = successfulLogin ? "a successful" : "an unsuccessful";
        String contentToAppend = String.format("User %s had %s login on %s at %s.%n", username, success, date, time);

        // True argument for FileWriter constructor indicates to append
        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(contentToAppend);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This validates the password credentials. On success, this will redirect to the MainMenuForm and displays if there are any appointments within the next 15 minutes.
     */
    private void passwordCheck(ActionEvent event) throws IOException {
        ObservableList<User> userData = UserDB.getAllUsers();
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isSuccessfulLogin = false;
        for (User entry: userData) {
            if (entry.getUsername().equals(username)) {
                if (entry.getPassword().equals(password)) {
                    FXMLLoader loader = Utilities.getLoader("MainMenuForm");

                    isSuccessfulLogin = true;
                    Utilities.setStage(loader, event, true);

                    // Generating the message if there is an appointment within 15 minutes of the current time.
                    StringBuilder appointmentMessage = new StringBuilder();
                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDateTime timeIn16Min = currentTime.plusMinutes(16);
                    for (Appointment appointmentEntry: AppointmentDB.getAllAppointments()) {
                        LocalDateTime startTime = appointmentEntry.getStart();
                        if (startTime.equals(currentTime) || (startTime.isBefore(timeIn16Min) && startTime.isAfter(currentTime))) {
                            appointmentMessage.append(String.format(Utilities.translate("appointmentIn15LineEntry"), Integer.toString(appointmentEntry.getAppointmentID()), appointmentEntry.getStart().toLocalDate().toString(), appointmentEntry.getStart().toLocalTime().toString()));
                        }
                    }

                    if (appointmentMessage.isEmpty()) {
                        appointmentMessage.append(Utilities.translate("noAppointments"));
                    } else {
                        appointmentMessage.insert(0, Utilities.translate("appointmentIn15Header") + "\n\n");
                    }

                    Utilities.showAlert(Alert.AlertType.INFORMATION, appointmentMessage.toString());

                    // Saving the current username for later.
                    Utilities.setCurrentUser(username);
                    break;
                }
            }
        }

        if (!isSuccessfulLogin) Utilities.showAlert(Alert.AlertType.ERROR, Utilities.translate("incorrectPassword"));

        writeUserDataToFile(username, isSuccessfulLogin);
    }

    /**
     * This allows for the user to hit the enter key and attempt a login
     */
    @FXML
    void onActionUsernameField(ActionEvent event) throws IOException {
        passwordCheck(event);
    }

    /**
     * This allows for the user to hit the enter key and attempt a login.
     */
    @FXML
    void onActionPasswordField(ActionEvent event) throws IOException{
        passwordCheck(event);
    }

    /**
     * Attempts a login when the go button is clicked.
     */
    @FXML
    void onActionGoBtn(ActionEvent event) throws IOException {
        passwordCheck(event);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String title = Utilities.translate("logIn");
        String username = Utilities.translate("username");
        String password = Utilities.translate("password");
        String goBtnLbl = Utilities.translate("go");
        String location = Utilities.translate("location");

        logInTitleLbl.setText(title);
        usernameFieldLbl.setText(username);
        passwordFieldLbl.setText(password);
        goBtn.setText(goBtnLbl);
        locationLbl.setText(location + ": " + ZoneId.systemDefault());
    }
}
