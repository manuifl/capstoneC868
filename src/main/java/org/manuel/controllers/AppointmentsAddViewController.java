/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.dao.DBCustomers;
import org.manuel.dao.DBLocations;
import org.manuel.models.Appointment;
import org.manuel.models.Customer;
import org.manuel.uicontrols.AlertBoxes;
import org.manuel.uicontrols.AutoCompleteComboBoxListener;
import org.manuel.utilities.formatters.CustomerStringConverter;
import org.manuel.utilities.formatters.StringFormatter;
import org.manuel.utilities.formatters.TZConvert;
import org.manuel.utilities.validator.ErrorControlRegister;
import org.manuel.utilities.validator.InputFieldRealTimeValidator;
import org.manuel.utilities.validator.ValidatorStylingProperties;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.manuel.models.Location;
//import validatedTextField.ValidatedTextField;
import org.manuel.uicontrols.ValidatedTextField;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class AppointmentsAddViewController implements Initializable {

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
    AutoCompleteComboBoxListener<Customer> acp;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private final ZoneId estZoneID = ZoneId.of("America/New_York");
    Stage stage;
    private Customer customer;

    /**
     * Function to convert LocalDateTime at system default ZoneId with the same
     * instant at UTC
     *
     * @param ldt LocalDateTime
     * @return Timestamp
     */
    public Timestamp convertAtLocalToUTC(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Callback<DatePicker, DateCell> dayCellFactory = this.getDayCellFactory();
        appointmentDatePicker.setDayCellFactory(dayCellFactory);
        setAppType();
        appointmentEndCB.setPlaceholder(new Label("Please Select a Start time"));
        appointmentStartCB.setPlaceholder(new Label("Please Select a Date"));
        setTextFieldRealTimeValidator();
        populateCustomersCB();
        appointmentLocationCB.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                LocalDate date = appointmentDatePicker.getValue();
                LocalTime localStartTime = appointmentStartCB.getValue();
                LocalTime localEndTime = appointmentEndCB.getValue();
                if (newValue) {
                    if (localStartTime != null && localEndTime != null) {
                        LocalDateTime startDateTime = LocalDateTime.of(date, localStartTime);
                        LocalDateTime endDateTime = LocalDateTime.of(date, localEndTime);
                        //Convert to UTC
                        Timestamp tsStart = convertAtLocalToUTC(startDateTime);
                        Timestamp tsEnd = convertAtLocalToUTC(endDateTime);

                        appointmentLocationCB.setItems(DBLocations.getOnlyAvailableLocations(tsStart, tsEnd));
                    } else {
                        appointmentLocationCB.setPlaceholder(new Label("Select a Start and End time"));
                    }
                }
            }
        });
    }

    private void populateCustomersCB() {
        Task<List<Customer>> task = new Task<>() {
            @Override
            public ObservableList<Customer> call() throws Exception {
                return DBCustomers.getAllActiveCustomers();
            }
        };
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });
        task.setOnSucceeded(e -> {
            customersCB.setItems((ObservableList<Customer>) task.getValue());
            customersCB.setConverter(new CustomerStringConverter(customersCB));
            acp = new AutoCompleteComboBoxListener<>(customersCB);
            if (customer != null) {
                for (Customer cu : customersCB.getItems()) {
                    if (customer.getId() == cu.getId()) {
                        customersCB.setValue(cu);
                        break;
                    }
                }
            }
        });
        Main.getDbExecutor().execute(task);
    }


    @FXML
    void handleSaveButton(ActionEvent event) {
        initializeValidationTextFields();
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        //Appointment values
        String title = appointmentTitleVTF.getText();
        String description = appointmentDescVTF.getText();
        String type = appointmentTypeCB.getValue();
        Location location = appointmentLocationCB.getValue();
        Customer customer = customersCB.getValue();
        LocalDate date = appointmentDatePicker.getValue();
        LocalTime localStartTime = appointmentStartCB.getValue();
        LocalTime localEndTime = appointmentEndCB.getValue();

        //FIRST LEVEL CHECK empty strings
        if (checkIfFormHasInputErrors()) {
            showInputErrors();
            //CONTINUE TO SECOND LEVEL CHECK
        } else {
            //combine local date time
            LocalDateTime startDateTime = LocalDateTime.of(date, localStartTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, localEndTime);
            int endBeforeStart = startDateTime.compareTo(endDateTime);
            //SECOND LEVEL CHECK comboBox end before start
            if (endBeforeStart > 0) {
                AlertBoxes.appointmentEndBeforeStartTimeConflict(ownerStage);
            } //SECOND LEVEL CHECK comboBox end equals to start
            else if (endBeforeStart == 0) {
                AlertBoxes.appointmentNoDurationTimeConflict(ownerStage);
            } //CONTINUE TO THIRD LEVEL CHECK
            else {
                //Convert LocalDateTime To UTC as a Timestamp
                Timestamp tsStart = convertAtLocalToUTC(startDateTime);
                Timestamp tsEnd = convertAtLocalToUTC(endDateTime);
                String customerDateTimeConflict = DBAppointment.checkCustomerDateTimeConflict(tsStart, tsEnd, customer.getId());
                //THIRD LEVEL CHECK previous date conflicts
                if (customerDateTimeConflict != null) {
                    AlertBoxes.hostConflict(ownerStage, customerDateTimeConflict);
                } // CONTINUE TO FOURTH LEVEL CHECK
                else {
                    String locationConflict = DBAppointment.checkLocationDateTimeConflict(tsStart, tsEnd, location.getLocationId());
                    //FOURTH LEVEL CHECK previous location conflicts
                    if (locationConflict != null) {
                        AlertBoxes.locationConflict(ownerStage, locationConflict);
                    } else { //ADD APPOINTMENT
                        Appointment newAppointment = new Appointment(StringFormatter.toUpperFirst(title), StringFormatter.toUpperFirst(description), type, location, tsStart, tsEnd, customer, Main.getUser());
                        DBAppointment.addAppointment(newAppointment);

                        AlertBoxes.appointmentAdded(ownerStage, title, type);
                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.close();
                    }
                }
            }
        }
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
    void handleAppointmentEndCB(ActionEvent event) {
        appointmentLocationCB.getSelectionModel().clearSelection();
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        AlertBoxes.unsavedChanges(stage);
    }

    private void setAppType() {
        ObservableList<String> appTypeList = FXCollections.observableArrayList();
        appTypeList.addAll("Training", "Planning", "Status Update", "Decision-Making", "Problem-Solving", "Team-Building", "Collaboration", "Other");
        appointmentTypeCB.getItems().addAll(appTypeList);

    }

    /**
     * Sets comboBoxes for start time and end time according to the local time
     * equivalent of 8am to 10am in eastern standard time. This method is called
     * by {@code handleAppointmentDatePicker}
     */
    private void setCBTimes() {
        //Get Selected LocalDate
        LocalDate selectedDate = appointmentDatePicker.getValue();
        //LocalDateTime
        LocalDateTime localDateTimeStart = LocalDateTime.of(selectedDate, Main.getOpenTime());
        LocalDateTime localDateTimeEnd = LocalDateTime.of(selectedDate, Main.getCloseTime().minusMinutes(30));
        //convert local time to EST Zoned Date Time
        ZonedDateTime startldtAtEST = TZConvert.getEquivalentToEST(localDateTimeStart);
        ZonedDateTime endldtAtEST = TZConvert.getEquivalentToEST(localDateTimeEnd);
        while (startldtAtEST.isBefore(endldtAtEST.plusSeconds(1))) {
            appointmentStartCB.getItems().add(startldtAtEST.toLocalTime());
            startldtAtEST = startldtAtEST.plusMinutes(30);

        }

    }


    private void setCBEndTimes() {
        LocalTime start = appointmentStartCB.getValue().plusMinutes(30);
        //Get Selected LocalDate
        LocalDate selectedDate = appointmentDatePicker.getValue();
        //LocalDateTime
        LocalDateTime localDateTimeStart = LocalDateTime.of(selectedDate, start);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(selectedDate, Main.getCloseTime());
        //get local time equivalent to est time
        ZonedDateTime startldtAtEST = TZConvert.getEquivalentToEST(localDateTimeStart);
        ZonedDateTime endldtAtEST = TZConvert.getEquivalentToEST(localDateTimeEnd);
        while (startldtAtEST.isBefore(endldtAtEST.plusSeconds(1))) {
            appointmentEndCB.getItems().add(startldtAtEST.toLocalTime());
            startldtAtEST = startldtAtEST.plusMinutes(30);
        }

    }

    private Callback<DatePicker, DateCell> getDayCellFactory() {
        return (final DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || item.compareTo(today) < 0);
            }
        };
    }

    private boolean checkIfFormHasInputErrors() {
        Map<String, Object> formValues = ErrorControlRegister.getFormValues(formControlsPane);
        return !formValues.isEmpty();
    }

    private void initializeValidationTextFields() {
        appointmentTitleVTF.eval();
        appointmentDescVTF.eval();
    }

    private void setTextFieldRealTimeValidator() {
        InputFieldRealTimeValidator.validateTitleTextField(appointmentTitleVTF, appointmentTitleLabel);
        InputFieldRealTimeValidator.validateDescTextField(appointmentDescVTF, appointmentDescLabel);
        InputFieldRealTimeValidator.validateTypeCB(appointmentTypeCB, appointmentTypeLabel);
        InputFieldRealTimeValidator.validateLocationCB(appointmentLocationCB, appointmentLocationLabel);
        InputFieldRealTimeValidator.validateDatePicker(appointmentDatePicker, appointmentDateLabel);
        InputFieldRealTimeValidator.validateStartTimeCB(appointmentStartCB, appointmentStartLabel);
        InputFieldRealTimeValidator.validateEndTimeCB(appointmentEndCB, appointmentEndLabel);
        InputFieldRealTimeValidator.validateCustomersCB(customersCB, appointmentCustomerLabel);
    }

    private void showInputErrors() {
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

    public void fillAddAppointmentCustomerField(Customer customer) {
        this.customer = customer;
    }

}
