package steurer.software_two.controller;

import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.w3c.dom.Text;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentAddController implements Initializable {
    @FXML
    private Label headerLbl;
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
                false
        );

        if (isValidInput) {
            AppointmentDB.addAppointment(
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
     * Redirects to the MainMenuForm
     */
    @FXML
    void onActionCancelBtn(ActionEvent event) throws IOException {
        Utilities.cancelButton(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Translating view text
        headerLbl.setText(Utilities.translate("addAppointment"));
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




    }
}
