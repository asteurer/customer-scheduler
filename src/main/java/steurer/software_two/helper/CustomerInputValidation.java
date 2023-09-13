package steurer.software_two.helper;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import steurer.software_two.model.FirstLevelDivision;

import java.util.Objects;

/**
 * This is a helper class that checks the add/modify customer inputs.
 */
public class CustomerInputValidation {
    private String errorMessage;
    private String name;
    private String address;
    private String zip;
    private String phone;
    private String division;

    public CustomerInputValidation() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * This checks various TextFields to ensure that the input matches what is needed. If all inputs are valid, this sets the instance variables for the class. Otherwise, this sets the errorMessage instance variables with the errors generated.
     * @return true if the conditions are met, else false.
     */
    public boolean isValidInput(TextField nameField, TextField addressField, TextField zipField, TextField phoneField, ComboBox<String> divisionField) {
        StringBuilder errorMessages = new StringBuilder();
        // Validating the name field
        if (nameField.getText().trim().isEmpty()) {
            errorMessages.append(Utilities.translate("nameEmpty")).append("\n");
        } else if (nameField.getText().trim().length() > 50) {
            errorMessages.append(Utilities.translate("nameTooLong")).append("\n");
        } else {
            this.name = nameField.getText().trim();
        }

        // Validating the address field
        if (addressField.getText().trim().isEmpty()) {
            errorMessages.append(Utilities.translate("addressEmpty")).append("\n");
        } else if (addressField.getText().trim().length() > 100) {
            errorMessages.append(Utilities.translate("addressTooLong")).append("\n");
        } else {
            this.address = addressField.getText().trim();
        }

        // Validating the zip field
        if (zipField.getText().trim().isEmpty()) {
            errorMessages.append(Utilities.translate("zipEmpty")).append("\n");
        } else if (zipField.getText().trim().length() > 50) {
            errorMessages.append(Utilities.translate("zipTooLong")).append("\n");
        } else {
            this.zip = zipField.getText().trim();
        }

        // Validating the phone field
        if (phoneField.getText().trim().isEmpty()) {
            errorMessages.append(Utilities.translate("phoneEmpty")).append("\n");
        } else if (phoneField.getText().trim().length() > 50) {
            errorMessages.append(Utilities.translate("phoneTooLong")).append("\n");
        } else {
            this.phone = phoneField.getText().trim();
        }

        // Validating the division combo box
        if (divisionField.getSelectionModel().getSelectedItem() == null) {
            errorMessages.append(Utilities.translate("divisionEmpty")).append("\n");
        } else {
            this.division = divisionField.getSelectionModel().getSelectedItem();
        }

        if (!errorMessages.isEmpty()) {
            this.errorMessage = errorMessages.toString();
            return false;
        }
        return true;
    }
}

