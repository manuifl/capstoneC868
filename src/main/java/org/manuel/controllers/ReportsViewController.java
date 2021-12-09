/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import javafx.beans.binding.Bindings;
import javafx.stage.FileChooser;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.dao.DBCustomers;
import org.manuel.dao.DBLocations;
import org.manuel.models.Customer;
import org.manuel.uicontrols.AlertBoxes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.manuel.models.Location;
import org.manuel.uicontrols.AutoCompleteComboBoxListener;

/**
 * ReportsView FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class ReportsViewController implements Initializable {

    @FXML
    private AnchorPane reportsAP;

    @FXML
    private ComboBox<String> reportTypesCB;

    @FXML
    private Button generateReportButton;

    @FXML
    private Label stringBuilderHeaderLabel;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button clearButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label reportTitleLabel;

    private ComboBox<Location> locationsCB;
    private ComboBox<Customer> customersCB;
    AutoCompleteComboBoxListener<Customer> acp;

    private void initLocationCB() throws SQLException {
        locationsCB = new ComboBox<>();
        locationsCB.setPrefSize(209.0, 30.0);
        locationsCB.setLayoutX(330.0);
        locationsCB.setLayoutY(180.0);
        locationsCB.setPromptText("Select a Location");
        locationsCB.setItems(DBLocations.getAllLocations());
        reportsAP.getChildren().add(locationsCB);
    }

    private void initCustomersCB() throws SQLException {
        customersCB = new ComboBox<>();
        customersCB.setPrefSize(209.0, 30.0);
        customersCB.setLayoutX(330.0);
        customersCB.setLayoutY(180.0);
        customersCB.setPromptText("Select a Customer");
        customersCB.setItems(DBCustomers.getAllCustomers());
        acp = new AutoCompleteComboBoxListener<>(customersCB);
        reportsAP.getChildren().add(customersCB);
    }

    /**
     * Initializes the ReportsViewController class.
     *
     * @param url URL
     * @param rb  ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportTypesCB.setItems(getReportTypes());
        saveButton.disableProperty().bind(Bindings.isEmpty(reportTextArea.textProperty()));
    }


    /**
     * Handler for clearButton
     * @param event ActionEvent
     */
    @FXML
    void handleClearButton(ActionEvent event) {
        reportTextArea.clear();

    }

    /**
     * Handler for reportTypesCB. If the Contacts Schedule option is selected,
     * the contactsCB combo box is displayed, if any other option is selected
     * the contactsCB combo box is hidden.
     * @param event ActionEvent
     * @throws SQLException
     */
    @FXML
    void handleReportTypesCB(ActionEvent event) throws SQLException {
        String selectedReportType = reportTypesCB.getValue();

        switch (selectedReportType) {
            case "Appointment Types by Month" -> {
                if (reportsAP.getChildren().contains(locationsCB) || reportsAP.getChildren().contains(customersCB)) {
                    reportsAP.getChildren().remove(locationsCB);
                    reportsAP.getChildren().remove(customersCB);
                }
            }
            case "Customer Schedule" -> {
                reportsAP.getChildren().remove(locationsCB);
                initCustomersCB();
            }
            case "Location Schedule" -> {
                reportsAP.getChildren().remove(customersCB);
                initLocationCB();
            }
            default -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("WARNING");
                alert.setHeaderText("Please fill out all the input fields");
                alert.setContentText("One or more input fields have not been assigned a value");
                alert.showAndWait();
            }
        }

    }

    /**
     * Handler for generateReportButton. Executes switch case statement of all
     * three report types.
     *
     * @param event ActionEvent
     */
    @FXML
    void handleGenerateReportButton(ActionEvent event) {
        String selectedReportType = reportTypesCB.getValue();
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        if (reportTypesCB.getValue() == null) {
            AlertBoxes.noReportTypeSelected(ownerStage);
        }
        else {
            switch (selectedReportType) {
                case "Appointment Types by Month" -> {
                    reportTextArea.setText(DBAppointment.generateMonthTypeReport());
                    reportTitleLabel.setText("Appointment Types by Month:");
                    stringBuilderHeaderLabel.setText("Month | Qty | Type");
                }
                case "Customer Schedule" -> {
                    if (customersCB.getValue() == null) {
                        AlertBoxes.noCustomerSelectedForReport(ownerStage);
                    } else {
                        reportTextArea.setText(DBAppointment.generateCustomerScheduleReport(customersCB.getEditor().getText()));
                        reportTitleLabel.setText("Customer Schedule for: " + customersCB.getEditor().getText());
                        stringBuilderHeaderLabel.setText("ID | Date | Start | End | Title | Description | Type | Location");
                    }
                }
                case "Location Schedule" -> {
                    if (locationsCB.getValue() == null) {
                        AlertBoxes.noLocationSelectedForReport(ownerStage);
                    } else {
                        reportTextArea.setText(DBAppointment.generateLocationScheduleReport(locationsCB.getValue().toString()));
                        reportTitleLabel.setText("Location Schedule for: " + locationsCB.getValue().toString());
                        stringBuilderHeaderLabel.setText("ID | Date | Start | End | Title | Description | Type | Customer");
                    }
                }
                default -> {
                }
            }
        }

    }

    /**
     * Populates reportTypesCB
     *
     * @return ObservableList
     */
    public static ObservableList<String> getReportTypes() {
        ObservableList<String> reportTypes = FXCollections.observableArrayList();
        reportTypes.add("Appointment Types by Month");
        reportTypes.add("Customer Schedule");
        reportTypes.add("Location Schedule");
        return reportTypes;
    }

    @FXML
    void handleSaveButton(ActionEvent event) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy-hh-mm-ss");
        //Creating a File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName(reportTypesCB.getValue() + LocalDateTime.now().format(dateTimeFormatter));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(Main.getPrimaryStage());
        if (selectedFile != null) {
            // dialog closed by selecting a file to save the data to
            try (BufferedWriter br = Files.newBufferedWriter(selectedFile.toPath(), StandardCharsets.UTF_8)) {
                br.write(reportTextArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
