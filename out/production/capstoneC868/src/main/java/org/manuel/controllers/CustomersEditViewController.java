/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import org.manuel.Main;
import org.manuel.dao.*;
import org.manuel.models.*;
//import org.manuel.navigationMenu.MainNavigation;
import org.manuel.uicontrols.AlertBoxes;
import org.manuel.uicontrols.AutoCompleteComboBoxListener;
import org.manuel.uicontrols.SmallProgressIndicator;
import org.manuel.utilities.formatters.CityStringConverter;
import org.manuel.utilities.validator.ErrorControlRegister;
import org.manuel.utilities.validator.InputFieldRealTimeValidator;
import org.manuel.utilities.validator.ValidatorStylingProperties;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
//import validatedTextField.ValidatedTextField;
import org.manuel.uicontrols.ValidatedTextField;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class CustomersEditViewController implements Initializable {


    @FXML
    private Pane formControlsPane;
    @FXML
    private GridPane formGridPane;
    @FXML
    private Label customerIDFieldLabel;
    @FXML
    private Label customerIDLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private ValidatedTextField customerNameVTF;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private ValidatedTextField customerAddressVTF;
    @FXML
    private Label customerPostalCodeLabel;
    @FXML
    private ValidatedTextField customerPostalCodeVTF;
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private ValidatedTextField customerPhoneVTF;
    @FXML
    private ValidatedTextField customerEmailVTF;
    @FXML
    private Label customerEmailLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private ComboBox<UnitedStateOfAmerica> statesCB;
    @FXML
    private Label cityLabel;
    @FXML
    private StackPane citiesStackPane;
    @FXML
    private ComboBox<City> citiesCB;
    @FXML
    private Label citiesPlaceHolder;
    @FXML
    private Button cancelButton;
    @FXML
    private Button updateButton;

    private SmallProgressIndicator progressIndicator;

    AutoCompleteComboBoxListener<City> acp;
    ObservableList<UnitedStateOfAmerica> unitedUnitedStateOfAmericaList = FXCollections.observableArrayList();
    ObservableList<City> cityList = FXCollections.observableArrayList();
    Stage stage;
    private Customer customer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeRealTimeValidation();
        //Set State ComboBox Values
        statesCB.setItems(DBStates.getAllStates());
        citiesCB.setEditable(true);
    }
    private void populateCitiesCB() {
        Task<List<City>> task = new Task<>() {
            @Override
            public ObservableList<City> call() throws Exception {

                return DBCities.getAllCitiesForState(statesCB.getValue().getStateCode());
            }
        };
        task.setOnRunning(event -> {
            progressIndicator = new SmallProgressIndicator();
            citiesStackPane.getChildren().add(progressIndicator);
            citiesCB.setPromptText("Loading cities");
            citiesPlaceHolder.setText("Loading " + statesCB.getValue().getStateCode() + " cities");
        });
        task.setOnFailed(e-> {
            task.getException().printStackTrace();
        });
        task.setOnSucceeded(e-> {
            citiesCB.setPromptText("Select A City");
            progressIndicator.setProgress(1.0);
            progressIndicator.setVisible(false);
            citiesStackPane.getChildren().remove(1);
            cityList = (ObservableList<City>) task.getValue();
            citiesCB.setItems(cityList);
            citiesCB.setConverter(new CityStringConverter(citiesCB));
            acp = new AutoCompleteComboBoxListener<>(citiesCB);
            for (City ci: citiesCB.getItems()) {
                if (customer.getCity().getCityId() == ci.getCityId()) {
                    citiesCB.setValue(ci);
                    break;
                }
            }
        });
        Main.getDbExecutor().execute(task);
    }

    @FXML
    private void onActionStatesCB(ActionEvent event) {
        citiesCB.getItems().removeAll(citiesCB.getItems());
        populateCitiesCB();
    }

    /**
     * Initailizes listeners for
     * @see org.manuel.utilities.validator.InputFieldRealTimeValidator
     */
    public void initializeRealTimeValidation() {
        InputFieldRealTimeValidator.validateAddressTextField(customerAddressVTF, customerAddressLabel);
        InputFieldRealTimeValidator.validatePostalCodeTextField(customerPostalCodeVTF, customerPostalCodeLabel);
        InputFieldRealTimeValidator.validatePhoneTextField(customerPhoneVTF, customerPhoneLabel);
        InputFieldRealTimeValidator.validateStateCB(statesCB, stateLabel);
        InputFieldRealTimeValidator.validateCityCB(citiesCB, cityLabel);
        InputFieldRealTimeValidator.validateNameTextField(customerNameVTF, customerNameLabel);
        InputFieldRealTimeValidator.validateEmailTextField(customerEmailVTF, customerEmailLabel);
    }

    public boolean checkIfFormHasInputErrors() {
        Map<String, Object> formValues = ErrorControlRegister.getFormValues(formControlsPane);
        return !formValues.isEmpty();
    }

    public void initializeValidationTextFields() {
        customerNameVTF.eval();
        customerAddressVTF.eval();
        customerPhoneVTF.eval();
        customerPostalCodeVTF.eval();
        customerEmailVTF.eval();
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        AlertBoxes.unsavedChanges(stage);

    }

    @FXML
    void handleUpdateButton(ActionEvent event) {

        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        String id = customerIDLabel.getText();
        int cID = Integer.parseInt(id);
        String name = customerNameVTF.getText();
        String address = customerAddressVTF.getText();
        String postalCode = customerPostalCodeVTF.getText();
        String phone = customerPhoneVTF.getText();
        String email = customerEmailVTF.getText();


        City city = citiesCB.getValue();
        initializeValidationTextFields();

        if (checkIfFormHasInputErrors()) {
            showInputErrors();
        } else {

            if(DBCustomers.checkEmailConflictForModify(email, cID)) {
                AlertBoxes.customerEmailNotUnique(ownerStage);
            } else {
                Customer existingCustomer = new Customer();
                existingCustomer.setId(cID);
                existingCustomer.setName(name);
                existingCustomer.setAddress(address);
                existingCustomer.setPostalCode(postalCode);
                existingCustomer.setPhoneNumber(phone);
                existingCustomer.setEmailAddress(email);
                existingCustomer.setCity(city);
                DBCustomers.modifyCustomer(existingCustomer);
                String customerIdString = customerIDLabel.getText();
                AlertBoxes.customerEdited(ownerStage, customerIdString, name);

                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }
    }

    public void fillCustomerTextFields(Customer customer) {
        this.customer = customer;
        customerIDLabel.setText(String.valueOf(customer.getId()));
        customerNameVTF.setText(customer.getName());
        customerAddressVTF.setText(customer.getAddress());
        customerPostalCodeVTF.setText(customer.getPostalCode());
        customerPhoneVTF.setText(customer.getPhoneNumber());
        customerEmailVTF.setText(customer.getEmailAddress());

        for (UnitedStateOfAmerica s : statesCB.getItems()) {
            if (customer.getCity().getState().getStateCode().equalsIgnoreCase(s.getStateCode())) {
                statesCB.setValue(s);
                break;
            }
        }

        populateCitiesCB();
    }
    private void showInputErrors() {
        Stage ownerStage = (Stage) formControlsPane.getScene().getWindow();
        AlertBoxes.fixErrorsMessage(ownerStage);
        if (customerNameVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(customerNameLabel);
        }
        if (customerAddressVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(customerAddressLabel);
        }
        if (customerPostalCodeVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(customerPostalCodeLabel);
        }
        if (customerPhoneVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(customerPhoneLabel);
        }
        if (customerEmailVTF.getAllErrors().contains(ValidatedTextField.Errors.EMPTY)) {
            ValidatorStylingProperties.emptyTextFieldLabel(customerEmailLabel);
        }
        if (statesCB.getValue() == null) {
            ValidatorStylingProperties.unselectedControlLabel(stateLabel);
        }
        if (citiesCB.getValue() == null) {
            ValidatorStylingProperties.unselectedControlLabel(cityLabel);
        }
    }
}
