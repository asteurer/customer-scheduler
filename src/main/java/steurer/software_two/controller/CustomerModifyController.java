package steurer.software_two.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.w3c.dom.Text;
import steurer.software_two.DAO.CountryDB;
import steurer.software_two.DAO.CustomerDB;
import steurer.software_two.DAO.FirstLevelDivisionDB;
import steurer.software_two.helper.CustomerInputValidation;
import steurer.software_two.helper.Utilities;
import steurer.software_two.model.Country;
import steurer.software_two.model.Customer;
import steurer.software_two.model.FirstLevelDivision;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerModifyController implements Initializable {
    @FXML
    private Label titleLbl;
    @FXML
    private Label idLbl;
    @FXML
    private Label nameLbl;
    @FXML
    private Label addressLbl;
    @FXML
    private Label zipLbl;
    @FXML
    private Label phoneLbl;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField zipField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ComboBox<String> countryBox;
    @FXML
    private ComboBox<String> divisionBox;

    /**
     * When a country is selected, this places the corresponding divisions into the division combo box.
     */
    @FXML
    void onActionCountryBox(ActionEvent event) {
        String countrySelection = countryBox.getSelectionModel().getSelectedItem();
        ObservableList<FirstLevelDivision> divisions = FirstLevelDivisionDB.getAllDivisions();
        ObservableList<String>filteredDivisions = FXCollections.observableArrayList();

        for (FirstLevelDivision entry: divisions) {
            String countryName = Utilities.getCountryFromDivision(entry.getDivisionID());
            if (Objects.equals(countryName, countrySelection)) {
                filteredDivisions.add(entry.getDivision());
            }
        }
        divisionBox.setItems(filteredDivisions);
    }

    /**
     * When the save button is clicked, this validates and sends the input data to the database.
     */
    @FXML
    void onActionSaveBtn(ActionEvent event) throws IOException {
        CustomerInputValidation validator = new CustomerInputValidation();
        boolean isValidInput = validator.isValidInput(nameField, addressField, zipField, phoneField, divisionBox);

        if (isValidInput) {
            CustomerDB.modifyCustomer(new Customer(Integer.parseInt(idField.getText()), validator.getName(), validator.getAddress(), validator.getZip(), validator.getPhone(), countryBox.getSelectionModel().getSelectedItem(), divisionBox.getSelectionModel().getSelectedItem()));
            FXMLLoader loader = Utilities.getLoader("MainMenuForm");
            Utilities.setStage(loader, event, "MainMenuForm", true);

        } else {
            Utilities.showAlert(Alert.AlertType.ERROR, validator.getErrorMessage());
        }
    }

    /**
     * Redirects to the MainMenuForm.
     */
    @FXML
    void onActionCancelBtn(ActionEvent event) throws IOException {
        Utilities.cancelButton(event);
    }

    /**
     * This allows us to send customer data from the table in the MainMenuForm and place the data in the CustomerModifyForm.
     */
    public void sendCustomerToModify(Customer customer) {
        idField.setText(Integer.toString(customer.getId()));
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        zipField.setText(customer.getZip());
        phoneField.setText(customer.getPhone());
        countryBox.getSelectionModel().select(customer.getCountry());
        divisionBox.getSelectionModel().select(customer.getDivision());

        //Initializing the division combo box to have options based upon the initialized country
        ObservableList<FirstLevelDivision> divisions = FirstLevelDivisionDB.getAllDivisions();
        ObservableList<String>filteredDivisions = FXCollections.observableArrayList();

        for (FirstLevelDivision entry: divisions) {
            String countryName = Utilities.getCountryFromDivision(entry.getDivisionID());
            if (Objects.equals(countryName, customer.getCountry())) {
                filteredDivisions.add(entry.getDivision());
            }
        }
        divisionBox.setItems(filteredDivisions);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String>divisionInit = FXCollections.observableArrayList(Utilities.translate("pleaseSelectACountry"));
        ObservableList<String>countryNames = FXCollections.observableArrayList();

        ObservableList<Country>allCountries = CountryDB.getAllCountries();
        for (Country entry: allCountries) {
            countryNames.add(entry.getName());
        }

        //Translating text
        titleLbl.setText(Utilities.translate("addCustomer"));
        idLbl.setText(Utilities.translate("id"));
        nameLbl.setText(Utilities.translate("name"));
        addressLbl.setText(Utilities.translate("address"));
        zipLbl.setText(Utilities.translate("zip"));
        phoneLbl.setText(Utilities.translate("phone"));
        saveBtn.setText(Utilities.translate("save"));
        cancelBtn.setText(Utilities.translate("cancel"));
        countryBox.setPromptText(Utilities.translate("country"));
        divisionBox.setPromptText(Utilities.translate("division"));

        //Loading combo boxes with data
        countryBox.setItems(countryNames);
        divisionBox.setItems(divisionInit);

        //Disabling the id field
        idField.setDisable(true);
    }
}
