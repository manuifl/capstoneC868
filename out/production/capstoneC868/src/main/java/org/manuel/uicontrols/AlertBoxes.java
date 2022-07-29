/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.uicontrols;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.dao.DBCustomers;

import java.io.File;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * This class holds most alert boxes used though out the program
 *
 * @author Manuel Fuentes
 */
public class AlertBoxes {

    private static ImageView setSuccessImage() {
        File infoIconFile = new File("src/main/resources/org/manuel/Images/successIcon.png");
        ImageView iv = new ImageView();
        iv.setFitWidth(48.0);
        iv.setFitHeight(48.0);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        Image infoIconImage = new Image(infoIconFile.toURI().toString());
        iv.setImage(infoIconImage);
        return iv;
    }

    public static void fixErrorsMessage(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //alert.initModality(Modality.NONE);
        alert.setTitle("WARNING");
        alert.setHeaderText("Please fix the fields marked in red");
        alert.setContentText("Hover your mouse over the red text to see what needs to be fixed");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to info
     *
     * @param ownerStage
     * @param custName String
     */
    public static void customerAdded(Stage ownerStage, String custName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Customer Added");
        alert.setHeaderText("Customer Added!");
        alert.setContentText(custName + " was added to Customer.");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to inform of successful customer edit
     *
     * @param ownerStage
     * @param custId String
     * @param custName String
     */
    public static void customerEdited(Stage ownerStage, String custId, String custName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Customer Information Saved");
        alert.setHeaderText("Customer Information Saved!");
        alert.setContentText("Changes made to " + custName + " with ID#[" + custId + "]" + " have been saved.");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to confirm customer delete, uses Lambda expression to get the
     * response value.
     *
     * @param ownerStage
     * @param custId int customer ID
     * @param appoQty int number appointments assigned to the customer ID
     * @param custName String customer name
     */
    public static void customerConfirmDelete(Stage ownerStage, int custId, int appoQty, String custName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete the record [" + custId + "]" + custName + " and (" + appoQty + ") appointments, do you want to continue?");
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Deleting '" + custName + "' from Database");
        alert.initOwner(ownerStage);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent((ButtonType response) -> {
                    DBCustomers.deleteCustomer(custId);
                    //System.out.println(s1);
                    AlertBoxes.customerDeleted(custName);
                });
    }

    /**
     * AlertBox to inform of successful customer deletion
     *
     * @param custName String customer name
     */
    public static void customerDeleted(String custName) {
        Stage ownerStage = Main.getPrimaryStage();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Customer Deleted");
        alert.setHeaderText("Customer Deleted Succesfully!");
        alert.setContentText(custName + " was successfully deleted from the database.");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    public static void customerEmailNotUnique(Stage ownerStage){
        Alert alert = new Alert(Alert.AlertType.WARNING, "Email Conflict");
        alert.setTitle("Email is taken");
        alert.setHeaderText("Email is already associated to a customer");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to warn of previous appointment conflict existing for the
     * selected customer.
     *
     * @param ownerStage Owner Stage
     * @param content Alert content

     */
    public static void hostConflict(Stage ownerStage, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Host Conflict");
        alert.setContentText(content);
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to warn of previous appointment conflict existing for the
     * selected location.
     *
     * @param ownerStage Owner Stage
     * @param content Alert content
     */
    public static void locationConflict(Stage ownerStage, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Location Conflict");
        alert.setContentText(content);
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to warn of appointment end time being before the start time
     * @param ownerStage
     */
    public static void appointmentEndBeforeStartTimeConflict(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Appointment End Time is Before Start Time");
        alert.setHeaderText("Invalid Appointment End Time");
        alert.setContentText("Appointment cannot end before it has started. Please select an approiate time interval!");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to warn of appointment end time being the same as the start time
     * @param ownerStage
     */
    public static void appointmentNoDurationTimeConflict(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Appointment Has No Duration");
        alert.setHeaderText("Appointment Has No Duration");
        alert.setContentText("Appointment must have a duration. Please select an appropiate time interval!");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to confirm appointment delete, uses <b>Lambda expression </b>to
     * get the response value.If the user presses the OK button, a call is made
 to DBAppointment.deleteAppointment()
     *
     * @param ownerStage
     * @param appoId String value of appointment ID
     * @param appoTitle String appointment Title
     * @param appoType String appointmentType
     * @param appId int appointment ID
     */
    public static void appointmentConfirmDelete(Stage ownerStage, String appoId, String appoTitle, String appoType, int appId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete the record #[" + appoId + "] '" + appoTitle + "' of type [" + appoType + "], do you want to continue?");
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Deleting '" + appoTitle + "' from Database");
        alert.initOwner(ownerStage);
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent((ButtonType response) -> {
                    DBAppointment.deleteAppointment(appId);
                    AlertBoxes.appointmentDeleted(appoTitle, appoType);
                });
    }
    

    /**
     * AlertBox to inform of successful appointment addition
     *
     * @param ownerStage
     * @param appoTitle String appointment title
     * @param appoType String appointment type
     */
    public static void appointmentAdded(Stage ownerStage, String appoTitle, String appoType) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Appointment Added");
        alert.setHeaderText("Appointment Added!");
        alert.setContentText("Appointment [" + appoTitle + "] of type [" + appoType + "] was added to the database");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to inform of successful appointment modification
     *
     * @param ownerStage
     * @param appoId String value of appointment ID
     * @param custName String customer name
     */
    public static void appointmentEdited(Stage ownerStage, String appoId, String custName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Appointment Information Updated");
        alert.setHeaderText("Appointment Information Updated!");
        alert.setContentText("Changes made to appointment ID #[" + appoId + "] for customer [" + custName + "] have been updated.");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to inform of successful appointment deletion
     *
     * @param appoTitle String appointment name
     * @param appoType String appointment type
     */
    public static void appointmentDeleted(String appoTitle, String appoType) {
        Stage ownerStage = Main.getPrimaryStage();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Deletion Successful");
        alert.setHeaderText("Deletion Successful!");
        alert.setContentText("Appointment [" + appoTitle + "] of type [" + appoType + "] was successfully deleted from the database.");
        alert.setGraphic(setSuccessImage());
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    /**
     * AlertBox to inform that no table row was selected prior to clicking the
     * edit button
     *
     * @param ownerStage
     */
    public static void noItemSelectedtoEdit(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No Item Selected To Edit");
        alert.setContentText("No table item was selected. To edit, select  from the table, then click the Edit button.");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }
    
    /**
     * AlertBox to inform that no appointment was selected in the calendar prior to clicking the
     * edit button
     *
     * @param ownerStage
     */
    public static void noAppointmentelectedtoEdit(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No Appointment Selected To Edit");
        alert.setContentText("No appointment was selected. To edit, select an appointment, then click the Edit button.");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }
    

    /**
     * AlertBox to inform that no table row was selected prior to clicking the
     * edit button
     * @param ownerStage
     */
    public static void noItemSelectedtoDelete(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No Item Selected To Delete");
        alert.setContentText("No table item was selected. To delete, select an item from the table, then click the Delete button.");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }

    public static void noReportTypeSelected(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No Report Type Selected");
        alert.setContentText("Please select a report type");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }


    /**
     * Alert that no contact was selected for Contact Schedule Report
     * @param ownerStage
     */
    public static void noContactSelectedForReport(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Contact Selected");
        alert.setHeaderText("Please Select A Contact");
        alert.setContentText("Please select a contact to generate the schedule report");
        alert.initOwner(ownerStage);
        alert.showAndWait();

    }

    public static void noCustomerSelectedForReport(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("No Customer Selected");
        alert.setContentText("Please type or select a customer to generate a report");
        alert.initOwner(ownerStage);
        alert.showAndWait();
    }
    
    /**
     * Alert that no contact was selected for Contact Schedule Report
     * @param ownerStage
     */
    public static void noLocationSelectedForReport(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Location Selected");
        alert.setHeaderText("Please Select A Location");
        alert.setContentText("Please select a location to generate the schedule report");
        alert.initOwner(ownerStage);
        alert.showAndWait();

    }

    public static void unsavedChanges(Stage ownerStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Unsaved changes will be discarded, do you want to continue?");
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Unsaved Changes will be discarded");
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(ownerStage);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.OK) {
            ownerStage = (Stage) alert.getOwner().getScene().getWindow();
            ownerStage.close();
        }

    }
    

}
