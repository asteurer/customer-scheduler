package steurer.software_two.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import steurer.software_two.DAO.AppointmentDB;
import steurer.software_two.DAO.CustomerDB;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Appointment;
import steurer.software_two.model.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;



public class MainMenuController implements Initializable {
    @FXML
    private Label mainMenuTitle;
    @FXML
    private Label customersLbl;
    @FXML
    private Label appointmentsLbl;
    @FXML
    private Label reportsLbl;
    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button addAppointmentBtn;
    @FXML
    private Button modifyCustomerBtn;
    @FXML
    private Button modifyAppointmentBtn;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private Button deleteAppointmentBtn;
    @FXML
    private Button appointmentReportBtn;
    @FXML
    private Button customerReportBtn;
    @FXML
    private Button contactReportBtn;
    @FXML
    private RadioButton allRBtn;
    @FXML
    private RadioButton weekRBtn;
    @FXML
    private RadioButton monthRBtn;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<String, Customer> customerNameCol;
    @FXML
    private TableColumn<String, Customer> customerAddressCol;
    @FXML
    private TableColumn<String, Customer> customerZipCol;
    @FXML
    private TableColumn<String, Customer> customerPhoneCol;
    @FXML
    private TableColumn<String, Customer> customerCountryCol;
    @FXML
    private TableColumn<String, Customer> customerDivisionCol;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Integer, Appointment> appointmentIDCol;
    @FXML
    private TableColumn<String, Appointment> appointmentTitleCol;
    @FXML
    private TableColumn<String, Appointment> appointmentDescriptionCol;
    @FXML
    private TableColumn<String, Appointment> appointmentLocationCol;
    @FXML
    private TableColumn<Integer, Appointment> appointmentContactIDCol;
    @FXML
    private TableColumn<String, Appointment> appointmentTypeCol;
    @FXML
    private TableColumn<LocalDateTime, Appointment> appointmentStartCol;
    @FXML
    private TableColumn<LocalDateTime, Appointment> appointmentEndCol;
    @FXML
    private TableColumn<Integer, Appointment> appointmentCustomerID;
    @FXML
    private TableColumn<Integer, Appointment> appointmentUserID;

    /**
     * Redirects to the CustomerAddForm
     */
    @FXML
    void onActionAddCustomerBtn(ActionEvent event) throws IOException{
        FXMLLoader loader = Utilities.getLoader("CustomerAddForm");
        Utilities.setStage(loader, event, "MainMenuForm", false);
    }

    /**
     * Redirects to the CustomerModifyForm and sends the data selected in the customerTableView to the form.
     */
    @FXML
    void onActionModifyCustomerBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("CustomerModifyForm");
        CustomerModifyController controller = loader.getController();
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            Utilities.showAlert(Alert.AlertType.ERROR, Utilities.translate("modifyCustomerError"));
        } else {
            controller.sendCustomerToModify(selectedCustomer);
            Utilities.setStage(loader, event, "MainMenuForm", false);
        }
    }

    /**
     * Deletes the selected customerTableView entry from the database.
     */
    @FXML
    void onActionDeleteCustomerBtn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Utilities.translate("deleteConfirmation"));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ObservableList<Appointment> allAppointments = AppointmentDB.getAllAppointments();
                Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

                //Deleting all appointments associated with customer.
                AppointmentDB.deleteAppointment(selectedCustomer.getId(), true);
                appointmentTableView.setItems(AppointmentDB.getAllAppointments());

                CustomerDB.deleteCustomer(selectedCustomer.getId());
                customerTableView.setItems(CustomerDB.getAllCustomers());

                Utilities.showAlert(Alert.AlertType.INFORMATION, String.format(Utilities.translate("deleteCustomerConfirmation"), selectedCustomer.getName()));

            } catch (Exception e) {
                Utilities.showAlert(Alert.AlertType.ERROR, Utilities.translate("deleteCustomerError"));
            }
        }
    }

    /**
     * Places all appointments in the database into the appointmentTableView when the "All" RadioButton is clicked.
     */
    @FXML
    void onActionAllRBtn(ActionEvent event){
        appointmentTableView.setItems(AppointmentDB.getAllAppointments());
    }

    /**
     * Places all appointments in the database that are 7 days from now to the appointmentTableView when the "Week" RadioButton is clicked.
     * LAMBDA EXPRESSION: This uses a lambda expression to simplify the code by eliminating a need for a for loop.
     */
    @FXML
    void onActionWeekRBtn(ActionEvent event) {
        LocalDate minDate = LocalDate.now().minusDays(1);
        LocalDate maxDate = minDate.plusDays(8);
        ObservableList<Appointment> filteredAppointments = AppointmentDB.getAllAppointments().filtered(entry -> {
            LocalDate startDate = entry.getStart().toLocalDate();
            return startDate.isBefore(maxDate) && startDate.isAfter(minDate);
        });

        appointmentTableView.setItems(filteredAppointments);

        if (filteredAppointments.isEmpty()) {
            Utilities.showAlert(Alert.AlertType.INFORMATION, Utilities.translate("noAppointmentsThisWeek"));
        }
    }

    /**
     *
     * Places all appointments in the database that are contained within "now's" month when the "Month" RadioButton is clicked.
     * LAMBDA EXPRESSION: This uses a lambda expression to simplify the code by eliminating a need for a for loop.
     */
    @FXML
    void onActionMonthRBtn(ActionEvent event) {
        Month currentMonth = LocalDate.now().getMonth();
        ObservableList<Appointment> filteredAppointments = AppointmentDB.getAllAppointments().filtered(entry -> {
            Month entryMonth = entry.getStart().getMonth();
            return entryMonth == currentMonth;
        });

        appointmentTableView.setItems(filteredAppointments);

        if (filteredAppointments.isEmpty()) {
            Utilities.showAlert(Alert.AlertType.INFORMATION, Utilities.translate("noAppointmentsThisMonth"));
        }
    }

    /**
     * Redirects to the AppointmentAddForm.
     */
    @FXML
    void onActionAddAppointmentBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("AppointmentAddForm");
        Utilities.setStage(loader, event, "MainMenuForm", false);
    }

    /**
     * Redirects to the AppointmentModifyForm and sends the data selected in the appointmentTableView to the form.
     */
    @FXML
void onActionModifyAppointmentBtn(ActionEvent event) throws IOException     {
        FXMLLoader loader = Utilities.getLoader("AppointmentModifyForm");
        AppointmentModifyController controller = loader.getController();
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            Utilities.showAlert(Alert.AlertType.ERROR, Utilities.translate("modifyAppointmentError"));
        } else {
            controller.sendAppointmentToModify(selectedAppointment);
            Utilities.setStage(loader, event, "MainMenuForm", false);
        }
    }

    /**
     * Deletes the selected appointmentTableView entry from the database.
     */
    @FXML
    void onActionDeleteAppointmentBtn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Utilities.translate("deleteConfirmation"));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

                AppointmentDB.deleteAppointment(selectedAppointment.getAppointmentID());
                ObservableList<Appointment> allAppointments = AppointmentDB.getAllAppointments();
                appointmentTableView.setItems(allAppointments);

                Utilities.showAlert(Alert.AlertType.INFORMATION, String.format(Utilities.translate("deleteAppointmentConfirmation"), Integer.toString(selectedAppointment.getAppointmentID()), selectedAppointment.getType()));

            } catch (Exception e) {
                Utilities.showAlert(Alert.AlertType.ERROR, Utilities.translate("deleteAppointmentError"));
            }
        }
    }

    /**
     * Generates a report showing all current customers.
     */
    @FXML
    void onActionCustomerReportBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("ReportForm");
        ReportFormController controller = loader.getController();
        ObservableList<Customer> allCustomers = CustomerDB.getAllCustomers();
        StringBuilder reportText = new StringBuilder();
        String nameText = Utilities.translate("name");
        String idText = Utilities.translate("id");
        String zipText = Utilities.translate("zip");
        String phoneText = Utilities.translate("phone");

        for (Customer entry: allCustomers) {
            String name = entry.getName();
            String id = Integer.toString(entry.getId());
            String zip = entry.getZip();
            String phone = entry.getPhone();
            //Format is "- Name: name, ID: id, Zip: zip, Phone: phone"
            reportText.append(
                    String.format(
                            "- %s: %s, %s: %s, %s: %s, %s: %s%n",
                            nameText,
                            name,
                            idText,
                            id,
                            zipText,
                            zip,
                            phoneText,
                            phone
                            ));
        }

        controller.createReport("customerReport", reportText.toString());
        Utilities.setStage(loader, event, "MainMenuForm", false);
    }

    /**
     * Generates a report showing a count of all appointments grouped by type and by month.
     */
    @FXML
    void onActionAppointmentReportBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("ReportForm");
        ReportFormController controller = loader.getController();
        ObservableList<Appointment> allAppointments = AppointmentDB.getAllAppointments();
        Map<String, Integer> monthCounts = new HashMap<>();
        Map<String, Integer> typeCounts = new HashMap<>();
        StringBuilder monthText = new StringBuilder();
        StringBuilder typeText = new StringBuilder();

        monthText.append(Utilities.translate("month")).append("\n");
        typeText.append(Utilities.translate("type")).append("\n");

        for (Appointment entry : allAppointments) {
            String type = entry.getType();
            String monthNumber = entry.getStart().getMonth().toString();

            monthCounts.put(monthNumber, monthCounts.getOrDefault(monthNumber, 0) + 1);
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
        }

        // Building the month part of the report
        for (Map.Entry<String, Integer> entry: monthCounts.entrySet()) {
            monthText.append(String.format(
                    "    - %s: %s%n",
                    Utilities.translate(entry.getKey()),
                    entry.getValue()
            ));
        }

        // Building the type part of the report
        for (Map.Entry<String, Integer> entry: typeCounts.entrySet()) {
            typeText.append(String.format(
                    "    - %s: %s%n",
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        // The text will display as shown below:
        /*
        Month:
            - January: 2
            - February: 8
            ...
        Type:
            - Coffee Break: 2
            - Lunch: 8
            ...
        */
        controller.createReport("appointmentReport",  monthText.append(typeText).toString());
        Utilities.setStage(loader, event, "MainMenuForm", false);
    }

    /**
     * Generates a report showing every appointment grouped by Contact.
     */
    @FXML
    void onActionContactReportBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("ReportForm");
        ReportFormController controller = loader.getController();
        ObservableList<Appointment> allAppointments = AppointmentDB.getAllAppointments();
        Map<String, String> contactGrouping = new HashMap<>();
        StringBuilder reportText = new StringBuilder();
        String appointmentIDText = Utilities.translate("appointmentID");
        String titleText = Utilities.translate("title");
        String typeText = Utilities.translate("type");
        String descriptionText = Utilities.translate("description");
        String startText = Utilities.translate("start");
        String endText = Utilities.translate("end");
        String customerIDText = Utilities.translate("customerID");

        for (Appointment entry : allAppointments) {
            String contactName = Utilities.getContactName(entry.getContactID());
            String appointmentID = Integer.toString(entry.getAppointmentID());
            String title = entry.getTitle();
            String type = entry.getType();
            String description = entry.getDescription();
            String start = String.join(" ", entry.getStart().toString().split("T"));
            String end = String.join(" ", entry.getEnd().toString().split("T"));
            String customerID = Integer.toString(entry.getCustomerID());
            String inputString = String.format(
                    "    - %s: %s, %s: %s, %s: %s, %s: %s, %s: %s, %s: %s, %s: %s%n",
                    appointmentIDText,
                    appointmentID,
                    titleText,
                    title,
                    typeText,
                    type,
                    descriptionText,
                    description,
                    startText,
                    start,
                    endText,
                    end,
                    customerIDText,
                    customerID
            );

            // The text will display as below:
            /*
            Customer1:
                - Appointment ID: appointment id, Title: title, Type: type, Description: description, Start: start, End: end, Customer ID: customer id
            Customer2:
                - Appointment ID: appointment id, Title: title, Type: type, Description: description, Start: start, End: end, Customer ID: customer id
                - Appointment ID: appointment id, Title: title, Type: type, Description: description, Start: start, End: end, Customer ID: customer id
                ...
             */

            contactGrouping.put(contactName, contactGrouping.getOrDefault(contactName, "") + inputString);
        }

        // Building the report
        for (Map.Entry<String, String> entry: contactGrouping.entrySet()) {
            reportText.append(String.format(
                    "%s:%n%s%n",
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        controller.createReport("contactReport", reportText.toString());
        Utilities.setStage(loader, event, "MainMenuForm", false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Translating view text
        mainMenuTitle.setText(Utilities.translate("mainMenu"));
        customersLbl.setText(Utilities.translate("customers"));
        appointmentsLbl.setText(Utilities.translate("appointments"));
        reportsLbl.setText(Utilities.translate("reports"));
        addCustomerBtn.setText(Utilities.translate("addCustomer"));
        modifyCustomerBtn.setText(Utilities.translate("modifyCustomer"));
        addAppointmentBtn.setText(Utilities.translate("addAppointment"));
        modifyAppointmentBtn.setText(Utilities.translate("modifyAppointment"));
        customerReportBtn.setText(Utilities.translate("customerReport"));
        appointmentReportBtn.setText(Utilities.translate("appointmentReport"));
        contactReportBtn.setText(Utilities.translate("contactReport"));
        allRBtn.setText(Utilities.translate("all"));
        weekRBtn.setText(Utilities.translate("week"));
        monthRBtn.setText(Utilities.translate("month"));
        customerNameCol.setText(Utilities.translate("name"));
        customerAddressCol.setText(Utilities.translate("address"));
        customerZipCol.setText(Utilities.translate("zip"));
        customerPhoneCol.setText(Utilities.translate("phone"));
        customerCountryCol.setText(Utilities.translate("country"));
        customerDivisionCol.setText(Utilities.translate("division"));
        appointmentIDCol.setText(Utilities.translate("appointmentID"));
        appointmentTitleCol.setText(Utilities.translate("title"));
        appointmentDescriptionCol.setText(Utilities.translate("description"));
        appointmentLocationCol.setText(Utilities.translate("location"));
        appointmentContactIDCol.setText(Utilities.translate("contactID"));
        appointmentTypeCol.setText(Utilities.translate("type"));
        appointmentStartCol.setText(Utilities.translate("start"));
        appointmentEndCol.setText(Utilities.translate("end"));
        appointmentCustomerID.setText(Utilities.translate("customerID"));
        appointmentUserID.setText(Utilities.translate("userID"));
        deleteAppointmentBtn.setText(Utilities.translate("deleteAppointment"));
        deleteCustomerBtn.setText(Utilities.translate("deleteCustomer"));

        // Setting radio button default selection
        allRBtn.setSelected(true);

        // Generating the customer table
        customerTableView.setItems(CustomerDB.getAllCustomers());
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("zip"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));

        // Generating the appointment tables
        appointmentTableView.setItems(AppointmentDB.getAllAppointments());
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }
}
