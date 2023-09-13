package steurer.software_two.helper;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import steurer.software_two.DAO.AppointmentDB;
import steurer.software_two.model.Appointment;

import java.time.*;

/**
 * This validates the inputs for the Appointment-related FXML forms. Using the isValidInput method, if the inputs are valid, the instance variables of the class can accessed, otherwise, the errorMessage instance variable is assigned a string of error messages.
 */
public class AppointmentInputValidation {
    private String errorMessage;
    private String title;
    private String description;
    private String location;
    private String type;
    private int contactID;
    private int customerID;
    private LocalDateTime start;
    private LocalDateTime end;

    public AppointmentInputValidation() {}

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * This prepends the word start/end to a partial error bundle label, then translates the string and appends a linebreak character. This is specifically for date/time related errors.
     * @param bundleLabel The partial error bundle label that is to have the word "start" or "end" prepended.
     * @param isStart Indicates whether the date/time is a start or end value.
     * @return Returns a translated string.
     */
    private static String translateError(String bundleLabel, boolean isStart) {
        return Utilities.translate(isStart ? "start" + bundleLabel : "end" + bundleLabel) + "\n";
    }

    /**
     * This translates an error bundle label and appends a line break character.
     * @param bundleLabel The error bundle label that is to be translated.
     * @return Returns a translated string.
     */
    private static String translateError(String bundleLabel) {
        return Utilities.translate(bundleLabel) + "\n";
    }

    /**
     * This takes in JavaFX nodes related to date/time and validates the nodes.
     * @param isStart indicates whether the date time to be validated is a start date time or end date time
     * @return Returns a LocalDateTime if valid, else a string of errors
     */
    private static Object validateDateAndTime(DatePicker dateField, TextField timeField, RadioButton amRBtn, RadioButton pmRBtn, boolean isStart) {
        StringBuilder errorMessages = new StringBuilder();
        LocalDateTime dateTime = null;

        if (dateField.getValue() == null) {
            errorMessages.append(translateError("DateEmpty", isStart));
        } else if (timeField.getText().trim().isEmpty()) {
            errorMessages.append(translateError("TimeEmpty", isStart));
        } else if (!amRBtn.isSelected() & !pmRBtn.isSelected()) {
            errorMessages.append(translateError("AMPMNotSelected", isStart));
        } else {
            LocalDate date = dateField.getValue();
            String[] timeParts = timeField.getText().split(":");
            //Initializing the hour and minute values with a number that is not a valid hour or minute
            int hour = 100;
            int minute = 100;
            LocalTime time = null;

            try {
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            } catch (Exception e) {
                errorMessages.append(translateError("TimeInvalidFormat", isStart));
            }

            // Returning an error for a value like 13:00 AM
            if (amRBtn.isSelected() && (hour > 12 || hour <= 0)) {
                errorMessages.append(translateError("TimeInvalidValue", isStart));
            }

            // Adjusting for AM/PM
            if (pmRBtn.isSelected() && hour < 12) {
                hour += 12;
            } else if (amRBtn.isSelected() && hour == 12) {
                hour = 0;
            }

            try {
                time = LocalTime.of(hour, minute);
                dateTime = LocalDateTime.of(date, time);
            } catch (Exception e) {
                // If either the hour or minute have been detected as invalid, we don't want to print a second time-related error message.
                if (!(hour == 100) & !(minute == 100))
                    errorMessages.append(translateError("TimeInvalidValue"));
            }
        }

        if (errorMessages.isEmpty()) {
            return dateTime;
        } else {
            return errorMessages.toString();
        }
    }

    /**
     * This compares two sets of start and end LocaleDateTime objects and returns whether they overlap.
     * @return Returns whether the LocalDateTimes overlap (inclusive)
     */
    private boolean isOverlappingDateTime(LocalDateTime start1, LocalDateTime start2, LocalDateTime end1, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2) || start1.isEqual(start2) || end1.isEqual(end2);
    }

    /**
     * This looks at appointment start and end times and converts them from the local date time into Eastern Time and indicates whether the start and end time fall within office hours.
     * @return Returns whether the appointment is within office hours.
     */
    private boolean isWithinOfficeHours(LocalDateTime start, LocalDateTime end) {
        ZonedDateTime startET = start.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime endET = end.atZone(ZoneId.of("America/New_York"));

        LocalTime startOfficeHours = LocalTime.of(8, 0);
        LocalTime endOfficeHours = LocalTime.of(22, 0);

        return !startET.toLocalTime().isBefore(startOfficeHours) && !endET.toLocalTime().isAfter(endOfficeHours);
    }

    /**
     * This examines various nodes for the Appointment-related FXML forms and determines if the inputs are valid. If the inputs are valid, the instance variables (not errorMessage) of the superclass are set, else the errorMessage instance variable is assigned a string of errors.
     * @param isModify This indicates whether the input is intending to modify an existing entry.
     *  @return Returns whether the input is valid.
     */
    public boolean isValidInput(
            TextField titleField,
            TextField descriptionField,
            TextField locationField,
            TextField typeField,
            ComboBox<String> contactNameField,
            ComboBox<String> customerNameField,
            DatePicker startDate,
            TextField startTime,
            RadioButton startAM,
            RadioButton startPM,
            DatePicker endDate,
            TextField endTime,
            RadioButton endAM,
            RadioButton endPM,
            boolean isModify
    ) {

        StringBuilder errorMessages = new StringBuilder();

        // Validating the title field
        if (titleField.getText().trim().isEmpty()) {
            errorMessages.append(translateError("titleEmpty"));
        } else if (titleField.getText().trim().length() > 50) {
            errorMessages.append(translateError("titleTooLong"));
        } else {
            this.title = titleField.getText().trim();
        }

        // Validating the description field
        if (descriptionField.getText().trim().isEmpty()) {
            errorMessages.append(translateError("descriptionEmpty"));
        } else if (descriptionField.getText().trim().length() > 50) {
            errorMessages.append(translateError("descriptionTooLong"));
        } else {
            this.description = descriptionField.getText().trim();
        }

        // Validating the location field
        if (locationField.getText().trim().isEmpty()) {
            errorMessages.append(translateError("locationEmpty"));
        } else if (locationField.getText().trim().length() > 50) {
            errorMessages.append(translateError("locationTooLong"));
        } else {
            this.location = locationField.getText().trim();
        }

        // Validating the type field
        if (typeField.getText().trim().isEmpty()) {
            errorMessages.append(translateError("typeEmpty"));
        } else if (typeField.getText().trim().length() > 50) {
            errorMessages.append(translateError("typeTooLong"));
        } else {
            this.type = typeField.getText().trim();
        }

        // Validating the contact name field
        String contactName = contactNameField.getSelectionModel().getSelectedItem();
        if (contactName == null) {
            errorMessages.append(translateError("contactNameEmpty"));
        } else {
            int contactID = Utilities.getContactID(contactName);
            if (contactID != 0) {
                this.contactID = contactID;
            }
        }

        // Validating the customer name field
        String customerName = customerNameField.getSelectionModel().getSelectedItem();
        if (customerName == null) {
            errorMessages.append(translateError("customerNameEmpty"));
        } else {
            int customerID = Utilities.getCustomerID(customerName);
            if (customerID != 0) {
                this.customerID = customerID;
            }
        }

        // Beginning a check to ensure that the times are chronological, and that they don't overlap with other appointments
        boolean validStartTime = false;
        boolean validEndTime = false;

        // Validating the start date time
        Object startDateTime = validateDateAndTime(startDate, startTime, startAM, startPM, true);
        if (startDateTime instanceof String errorMsg) {
            errorMessages.append(errorMsg);
        } else {
            this.start = (LocalDateTime) startDateTime;
            validStartTime = true;
        }

        // Validating the end date time
        Object endDateTime = validateDateAndTime(endDate, endTime, endAM, endPM, false);
        if (endDateTime instanceof String errorMsg) {
            errorMessages.append(errorMsg);
        } else {
            this.end = (LocalDateTime) endDateTime;
            validEndTime = true;
        }

        if (validStartTime & validEndTime) {
            // Checking that the start date time is before or on the end date time
            if (this.start.isAfter(this.end)) {
                errorMessages.append(translateError("startEndNotInOrder"));
            // Checking to see if the start and end times are within office hours.
            } else if (!isWithinOfficeHours(this.start, this.end)) {
                errorMessages.append(translateError("officeHoursError"));
            } else if (this.customerID != 0){
                // Checking that the current appointment doesn't overlap with an existing appointment
                ObservableList<Appointment> allAppointments = AppointmentDB.getAllAppointments();
                for (Appointment entry: allAppointments) {
                    LocalDateTime entryStart = entry.getStart();
                    LocalDateTime entryEnd = entry.getEnd();
                    // If the entry's customer ID matches and the dates overlap...
                    if (entry.getCustomerID() == this.customerID && isOverlappingDateTime(this.start, entryStart, this.end, entryEnd)){
                        // If this is adding an appointment, or modifying an appointment and the date doesn't exactly match, add a line to the error message.
                        if (!isModify || Utilities.getCurrentAppointmentID() != entry.getAppointmentID()){
                            errorMessages.append(String.format(translateError("overlappingAppointment"), customerName, entry.getAppointmentID()));
                            break;
                        }
                    }
                }
            }

            // Checking to see if appointment spans multiple days
            if (!this.start.toLocalDate().equals(this.end.toLocalDate())) {
                errorMessages.append(translateError("appointmentSpanningMultipleDaysError"));
            }

            // If the appointment is in the past...
            if (this.start.isBefore(LocalDateTime.now())) {
                errorMessages.append(translateError("pastAppointment"));
            }
        }

        if (errorMessages.isEmpty()) {
            return true;
        } else {
            this.errorMessage = errorMessages.toString();
            return false;
        }
    }
}
