package steurer.software_two.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import steurer.software_two.DAO.AppointmentDB;
import steurer.software_two.DAO.ContactDB;
import steurer.software_two.DAO.CustomerDB;
import steurer.software_two.helper.AppointmentInputValidation;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Appointment;
import steurer.software_two.model.Contact;
import steurer.software_two.model.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentModifyController implements Initializable {
    @FXML
    private Label headerLbl;
    @FXML
    private Label appointmentIDLbl;
    @FXML
    private Label titleLbl;
    @FXML
    private Label descriptionLbl;
    @FXML
    private Label locationLbl;
    @FXML
    private Label typeLbl;
    @FXML
    private Label contactNameLbl;
    @FXML
    private Label customerNameLbl;
    @FXML
    private Label startLbl;
    @FXML
    private Label startDateLbl;
    @FXML
    private Label startTimeLbl;
    @FXML
    private Label endLbl;
    @FXML
    private Label endDateLbl;
    @FXML
    private Label endTimeLbl;
    @FXML
    private TextField appointmentIDField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField typeField;
    @FXML
    private ComboBox<String> contactNameField;
    @FXML
    private ComboBox<String> customerNameField;
    @FXML
    private DatePicker startDateField;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDateField;
    @FXML
    private TextField endTimeField;
    @FXML
    private RadioButton startAMRBtn;
    @FXML
    private RadioButton startPMRBtn;
    @FXML
    private RadioButton endAMRBtn;
    @FXML
    private RadioButton endPMRBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    /**
     * When the save button is clicked, this validates and sends the input data to the database.
     */
    @FXML
    void onActionSaveBtn(ActionEvent event) throws IOException {
        AppointmentInputValidation validator = new AppointmentInputValidation();
        boolean isValidInput = validator.isValidInput(
                titleField,
                descriptionField,
                locationField,
                typeField,
                contactNameField,
                customerNameField,
                startDateField,
                startTimeField,
                startAMRBtn,
                startPMRBtn,
                endDateField,
                endTimeField,
                endAMRBtn,
                endPMRBtn,
                true
        );

        if (isValidInput) {

            AppointmentDB.modifyAppointment(
                    Integer.parseInt(appointmentIDField.getText()),
                    validator.getTitle(),
                    validator.getDescription(),
                    validator.getLocation(),
                    validator.getType(),
                    validator.getStart(),
                    validator.getEnd(),
                    validator.getCustomerID(),
                    validator.getContactID()
            );

            Utilities.setStage(Utilities.getLoader("MainMenuForm"), event, "MainMenuForm", true);

        } else {
            Utilities.showAlert(Alert.AlertType.ERROR, validator.getErrorMessage());
        }
    }

    /**
     * @return Returns the hour and minute integers as a string joined by a colon.
     */
    private String joinHourAndMinutes(int hourValue, int minuteValue) {
        String minuteString = null;
        String hourString = Integer.toString(hourValue);

        if (minuteValue == 0) {
            minuteString = "00";
        } else {
            minuteString = Integer.toString(minuteValue);
        }

        if (hourString.length() == 1) {
            hourString = "0" + hourString;
        }

        return hourString + ":" + minuteString;
    }

    /**
     * This allows us to send appointment data from the table in the MainMenuForm and place the data in the AppointmentModifyForm.
     */
    public void sendAppointmentToModify(Appointment appointment) {
        //Parsing separating out the date and time values
        LocalDateTime start = appointment.getStart();
        LocalTime startTime = start.toLocalTime();
        LocalDate startDate = start.toLocalDate();
        String[] startDateParts = startTime.toString().split(":");
        int startHour = Integer.parseInt(startDateParts[0]);
        int startMinute = Integer.parseInt(startDateParts[1]);
        boolean startIsPM = false;

        if (startHour >= 12) {
            if (startHour > 12) startHour -= 12;
            startIsPM = true;
        } else if (startHour == 0) {
            startHour += 12;
        }

        LocalDateTime end = appointment.getEnd();
        LocalTime endTime = end.toLocalTime();
        LocalDate endDate = end.toLocalDate();
        String[] endDateParts = endTime.toString().split(":");
        int endHour = Integer.parseInt(endDateParts[0]);
        int endMinute = Integer.parseInt(endDateParts[1]);
        boolean endIsPM = false;

        if (endHour >= 12) {
            if (endHour > 12) endHour -= 12;
            endIsPM = true;
        } else if (endHour == 0) {
            endHour += 12;
        }

        // Setting a static variable for comparison on save:
        Utilities.setCurrentAppointmentID(appointment.getAppointmentID());

        // Generating field data
        appointmentIDField.setText(Integer.toString(appointment.getAppointmentID()));
        titleField.setText(appointment.getTitle());
        descriptionField.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        typeField.setText(appointment.getType());
        contactNameField.getSelectionModel().select(Utilities.getContactName(appointment.getContactID()));
        customerNameField.getSelectionModel().select(Utilities.getCustomerName(appointment.getCustomerID()));
        startDateField.setValue(startDate);
        startTimeField.setText(joinHourAndMinutes(startHour, startMinute));
        endDateField.setValue(endDate);
        endTimeField.setText(joinHourAndMinutes(endHour, endMinute));

        if (startIsPM) {
            startPMRBtn.setSelected(true);
        } else {
            startAMRBtn.setSelected(true);
        }

        if (endIsPM) {
            endPMRBtn.setSelected(true);
        } else {
            endAMRBtn.setSelected(true);
        }
    }

    /**
     * Redirects to the MainMenuForm.
     */
    @FXML
    void onActionCancelBtn(ActionEvent event) throws IOException {
        Utilities.cancelButton(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Translating view text
        headerLbl.setText(Utilities.translate("modifyAppointment"));
        appointmentIDLbl.setText(Utilities.translate("appointmentID"));
        titleLbl.setText(Utilities.translate("title"));
        descriptionLbl.setText(Utilities.translate("description"));
        locationLbl.setText(Utilities.translate("location"));
        typeLbl.setText(Utilities.translate("type"));
        contactNameLbl.setText(Utilities.translate("contactName"));
        customerNameLbl.setText(Utilities.translate("customerName"));
        startLbl.setText(Utilities.translate("start"));
        startDateLbl.setText(Utilities.translate("date"));
        startTimeLbl.setText(Utilities.translate("time"));
        endLbl.setText(Utilities.translate("end"));
        endDateLbl.setText(Utilities.translate("date"));
        endTimeLbl.setText(Utilities.translate("time"));
        saveBtn.setText(Utilities.translate("save"));
        cancelBtn.setText(Utilities.translate("cancel"));

        // Filling combo boxes
        ObservableList<Contact> allContacts = ContactDB.getAllContacts();
        ObservableList<String> allContactNames = allContacts.stream().map(Contact::getName).collect(Collectors.toCollection(FXCollections::observableArrayList));
        contactNameField.setItems(allContactNames);

        ObservableList<Customer> allCustomers = CustomerDB.getAllCustomers();
        ObservableList<String> allCustomerNames = allCustomers.stream().map(Customer::getName).collect(Collectors.toCollection(FXCollections::observableArrayList));
        customerNameField.setItems(allCustomerNames);

        // Disabling the appointment id field
        appointmentIDField.setDisable(true);
    }
}
