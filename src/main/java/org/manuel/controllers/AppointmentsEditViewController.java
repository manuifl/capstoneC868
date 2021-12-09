/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.dao.DBCustomers;
import org.manuel.dao.DBLocations;
import org.manuel.models.Appointment;
import org.manuel.models.Customer;
import org.manuel.models.Location;
import org.manuel.uicontrols.AlertBoxes;
import org.manuel.utilities.formatters.StringFormatter;
import org.manuel.utilities.formatters.TZConvert;
import org.manuel.utilities.validator.ErrorControlRegister;
import org.manuel.utilities.validator.InputFieldRealTimeValidator;
import org.manuel.utilities.validator.ValidatorStylingProperties;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
//import validatedTextField.ValidatedTextField;
import org.manuel.uicontrols.ValidatedTextField;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class AppointmentsEditViewController implements Initializable {

    @FXML
    private Pane formControlsPane;
    @FXML
    private GridPane formGridPane;
    @FXML
    private Label appointmentIdTextLabel;
    @FXML
    private Label appointmentIdValueLabel;
    @FXML
    private ValidatedTextField appointmentTitleVTF;
    @FXML
    private ValidatedTextField appointmentDescVTF;
    @FXML
    private ComboBox<String> appointmentTypeCB;
    @FXML
    private ComboBox<Location> appointmentLocationCB;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private ComboBox<LocalTime> appointmentStartCB;
    @FXML
    private ComboBox<LocalTime> appointmentEndCB;
    @FXML
    private ComboBox<Customer> customersCB;
    @FXML
    private Label appointmentTitleLabel;
    @FXML
    private Label appointmentDescLabel;
    @FXML
    private Label appointmentTypeLabel;
    @FXML
    private Label appointmentLocationLabel;
    @FXML
    private Label appointmentDateLabel;
    @FXML
    private Label appointmentStartLabel;
    @FXML
    private Label appointmentEndLabel;
    @FXML
    private Label appointmentCustomerLabel;

    @FXML
    private Button moreInfoButton2;
    @FXML
    private ImageView moreInfoImageView2;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    private final ZoneId estZoneID = ZoneId.of("America/New_York");
    Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldRealTimeValidator();
        setAppType();
        try {
            appointmentLocationCB.setItems(DBLocations.getAllLocations());
            appointmentLocationCB.showingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    LocalDate date = appointmentDatePicker.getValue();
                    LocalTime localStartTime = appointmentStartCB.getValue();
                    LocalTime localEndTime = appointmentEndCB.getValue();
                    if (newValue) {
                        if(localStartTime != null && localEndTime != null){
                            LocalDateTime startDateTime = LocalDateTime.of(date, localStartTime);
                            LocalDateTime endDateTime = LocalDateTime.of(date, localEndTime);
                            //Convert to UTC
                            Timestamp tsStart = TZConvert.convertAtLocalToUTC(startDateTime);
                            Timestamp tsEnd = TZConvert.convertAtLocalToUTC(endDateTime);
                            appointmentLocationCB.setItems(DBLocations.getOnlyAvailableLocations(tsStart, tsEnd));
                        }
                        else{
                            appointmentLocationCB.setPlaceholder(new Label("Select a Start and End time"));
                        }
                    }
                }
            });
            customersCB.setItems(DBCustomers.getAllActiveCustomers());
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentsEditViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        appointmentDatePicker.setValue(LocalDate.now());
        setCBTimes();
        Callback<DatePicker, DateCell> dayCellFactory = this.getDayCellFactory();
        appointmentDatePicker.setDayCellFactory(dayCellFactory);

    }

    @FXML
    void handleAppointmentDatePicker(ActionEvent event) {
        appointmentLocationCB.getSelectionModel().clearSelection();
        setCBTimes();
    }

    @FXML
    void handleAppointmentStartCB(ActionEvent event) {
        appointmentLocationCB.getSelectionModel().clearSelection();
        appointmentEndCB.getItems().clear();
        appointmentEndCB.setPromptText("Select end time");
        setCBEndTimes();

    }
    @FXML
    void handleAppointmentEndCB(ActionEvent event) throws SQLException {
        appointmentLocationCB.getSelectionModel().clearSelection();
    }

    @FXML
    void handleUpdateButton(ActionEvent event) {
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        initializeValidationTextFields();

        int appointmentID = Integer.parseInt(appointmentIdValueLabel.getText());
        String title = appointmentTitleVTF.getText();
        String description = appointmentDescVTF.getText();
        String type = appointmentTypeCB.getValue();
        Location location = appointmentLocationCB.getValue();
        Customer customer = customersCB.getValue();
        //capture dates and time in local time
        LocalDate date = appointmentDatePicker.getValue();
        LocalTime localStartTime = appointmentStartCB.getValue();
        LocalTime localEndTime = appointmentEndCB.getValue();

        //FIRST LEVEL CHECK empty strings
        if (checkIfFormHasInputErrors()) {
            showInputErrors();
        } else {
            //combine localdatetime
            LocalDateTime startDateTime = LocalDateTime.of(date, localStartTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, localEndTime);

            //Convert LocalDateTime To UTC as a Timestamp
            Timestamp tsStart = TZConvert.convertAtLocalToUTC(startDateTime);
            Timestamp tsEnd = TZConvert.convertAtLocalToUTC(endDateTime);

            //int endBeforeStart = selectedLocalStartTime.compareTo(selectedLocalEndTime);
            int endBeforeStart = startDateTime.compareTo(endDateTime);
            System.out.println(endBeforeStart);

            //SECOND LEVEL CHECK comboBox end before start
            if (endBeforeStart > 0) {
                AlertBoxes.appointmentEndBeforeStartTimeConflict(ownerStage);
            } //SECOND LEVEL CHECK comboBox end equals to start
            else if (endBeforeStart == 0) {
                AlertBoxes.appointmentNoDurationTimeConflict(ownerStage);
            } //CONTINUE TO THIRD LEVEL CHECK
            else {
                //FOURTH LEVEL CHECK previous date conflicts
                String customerConflict = DBAppointment.checkCustomerDateTimeConflictForModify(tsStart, tsEnd, customer.getId(), appointmentID);
                if (customerConflict == null) {
                    String locationConflict = DBAppointment.checkLocationDateTimeConflictForModify(tsStart, tsEnd, location.getLocationId(), appointmentID);
                    if (locationConflict == null) {
                        //UPDATE APPOINTMENT
                        Appointment newAppointment = new Appointment(appointmentID, StringFormatter.toUpperFirst(title), StringFormatter.toUpperFirst(description), type, location, tsStart, tsEnd, customer, Main.getUser());
                        DBAppointment.modifyAppointment(newAppointment);

                        AlertBoxes.appointmentEdited(ownerStage, String.valueOf(appointmentID), String.valueOf(customersCB.getValue()));
                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.close();
                    } else {
                        //LOCATION CONFLICT
                        AlertBoxes.locationConflict(ownerStage, locationConflict);
                    }
                } //CUSTOMER CONFLICT
                else {
                    AlertBoxes.hostConflict(ownerStage, customerConflict);
                }
            } //THIRD LEVEL CHECK
        }
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        AlertBoxes.unsavedChanges(stage);
    }


    public void fillEditAppointmentFields(Appointment appointment) {

        appointmentIdValueLabel.setText(String.valueOf(appointment.getId()));
        appointmentTitleVTF.setText(appointment.getTitle());
        appointmentDescVTF.setText(appointment.getDescription());

        //Get Selected appointments start time
        Timestamp startTimestamp = appointment.getStartTime();
        Timestamp endTimestamp = appointment.getEndTime();

        //convert database Timestamps to LocalDateTimes
        LocalDateTime startLDT = startTimestamp.toLocalDateTime();
        LocalDateTime endLDT = endTimestamp.toLocalDateTime();

        //set DatePicker Value
        LocalDate datePickerValue = startLDT.toLocalDate();

        LocalTime startTimeValue = startLDT.toLocalTime();
        LocalTime endTimeValue = endLDT.toLocalTime();

        appointmentDatePicker.setValue(datePickerValue);

        for (String appType : appointmentTypeCB.getItems()) {
            if (appointment.getType().equals(appType)) {
                appointmentTypeCB.setValue(appType);
                break;
            }
        }

        for (Location loc : appointmentLocationCB.getItems()) {
            if (appointment.getLocation().getLocationId() == loc.getLocationId()) {
                appointmentLocationCB.setValue(loc);
                break;
            }
        }

        for (LocalTime ltstart : appointmentStartCB.getItems()) {
            if (startTimeValue.compareTo(ltstart) == 0) {
                appointmentStartCB.setValue(ltstart);
                break;
            }
        }

        for (LocalTime ltend : appointmentEndCB.getItems()) {
            if (endTimeValue.compareTo(ltend) == 0) {
                appointmentEndCB.setValue(ltend);
                break;
            }
        }

        for (Customer cu : customersCB.getItems()) {
            if (appointment.getCustomer().getId() == cu.getId()) {
                customersCB.setValue(cu);
                break;
            }
        }

    }

    public void setAppType() {
        ObservableList<String> appTypeList = FXCollections.observableArrayList();
        appTypeList.addAll("Training", "Planning", "Status Update", "Decision-Making", "Problem-Solving", "Team-Building", "Collaboration", "Other");
        appointmentTypeCB.getItems().addAll(appTypeList);

    }



    /**
     * Sets comboBoxes for start time and end time according to the local time
     * equivalent of 8am to 10am in eastern standard time. This method is called
     * by {@code handleAppointmentDatePicker}
     */
    public void setCBTimes() {
        //Get Selected LocalDate
        LocalDate selectedDate = appointmentDatePicker.getValue();
        //LocalDateTime
        LocalDateTime localDateTimeStart = LocalDateTime.of(selectedDate, Main.getOpenTime());
        LocalDateTime localDateTimeEnd = LocalDateTime.of(selectedDate, Main.getCloseTime().minusMinutes(30));

        //get zoned datetime equivalent to est time
        ZonedDateTime startldtAtEST = TZConvert.getEquivalentToEST(localDateTimeStart);
        ZonedDateTime endldtAtEST = TZConvert.getEquivalentToEST(localDateTimeEnd);

        while (startldtAtEST.isBefore(endldtAtEST.plusSeconds(1))) {
            appointmentStartCB.getItems().add(startldtAtEST.toLocalTime());
            appointmentEndCB.getItems().add(startldtAtEST.toLocalTime());
            startldtAtEST = startldtAtEST.plusMinutes(30);
        }

    }

    public void setCBEndTimes() {
        LocalTime start = appointmentStartCB.getValue().plusMinutes(30);;
        //Get Selected LocalDate
        LocalDate selectedDate = appointmentDatePicker.getValue();

        //LocalDateTime
        LocalDateTime localDateTimeStart = LocalDateTime.of(selectedDate, start);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(selectedDate, Main.getCloseTime());

        //get zoned datetime equivalent to est time
        ZonedDateTime startldtAtEST = TZConvert.getEquivalentToEST(localDateTimeStart);
        ZonedDateTime endldtAtEST = TZConvert.getEquivalentToEST(localDateTimeEnd);

        while (startldtAtEST.isBefore(endldtAtEST.plusSeconds(1))) {
            appointmentEndCB.getItems().add(startldtAtEST.toLocalTime());
            startldtAtEST = startldtAtEST.plusMinutes(30);
        }

    }
    public Callback<DatePicker, DateCell> getDayCellFactory() {

        return (final DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || item.compareTo(today) < 0);
            }
        };
    }

    public boolean checkIfFormHasInputErrors() {
        Map<String, Object> formValues = ErrorControlRegister.getFormValues(formControlsPane);
        return !formValues.isEmpty();
    }

    public void initializeValidationTextFields() {
        appointmentTitleVTF.eval();
        appointmentDescVTF.eval();
    }

    public void setTextFieldRealTimeValidator() {
        InputFieldRealTimeValidator.validateTitleTextField(appointmentTitleVTF, appointmentTitleLabel);
        InputFieldRealTimeValidator.validateDescTextField(appointmentDescVTF, appointmentDescLabel);
    }

    public void showInputErrors() {
        Stage ownerStage = (Stage) formControlsPane.getScene().getWindow();
        AlertBoxes.fixErrorsMessage(ownerStage);

        if (appointmentTitleVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentTitleLabel);
        }
        if (appointmentDescVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentDescLabel);
        }
        if (appointmentDatePicker.getEditor().getText().isEmpty() || appointmentDatePicker.getEditor().getText().isBlank() || appointmentDatePicker.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentDateLabel);
        }
        if (appointmentTypeCB.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentTypeLabel);
        }
        if (appointmentLocationCB.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentLocationLabel);
        }
        if (appointmentStartCB.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentStartLabel);
        }
        if (appointmentEndCB.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentEndLabel);
        }
        if (customersCB.getValue() == null) {
            ValidatorStylingProperties.emptyTextFieldLabel(appointmentCustomerLabel);
        }
    }
}
