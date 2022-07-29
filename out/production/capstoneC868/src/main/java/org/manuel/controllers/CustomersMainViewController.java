/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.dao.DBCustomers;
import org.manuel.models.Appointment;
import org.manuel.models.Customer;
//import org.manuel.navigationMenu.MainNavigation;
import org.manuel.uicontrols.AlertBoxes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.manuel.uicontrols.PlaceHolderLabel;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class CustomersMainViewController {

    @FXML
    private ComboBox<String> searchByCB;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Customer> customersTableView;
    @FXML
    private Label customerTablePlaceHolder;
    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, String> customerAddressCol;

    @FXML
    private TableColumn<Customer, String> customerPostalCodeCol;

    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    @FXML
    private TableColumn<Customer, String> customerCityCol;

    @FXML
    private TableView<Appointment> customerAppointmentsTable;
    @FXML
    private Label appointmentsTablePlaceHolder;

    @FXML
    private TableColumn<String, Appointment> titleCol;

    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label customerNameLabel;




    @FXML
    private Button editButton;

    @FXML
    private Button editCustomerAppointments;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addButton;

    @FXML
    private Button makeAppointmentButton;

    public static Appointment appointment;
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public static Customer currCustomer;
    public static int currentCustomerID;
    private FilteredList<Customer> filteredData;
    private ProgressIndicator customersProgressIndicator;
    private ProgressIndicator appointmentsProgressIndicator;
    Stage stage;
    Parent parent;
    Scene scene;


    /**
     * Initializes the ManageCustomersController class.
     *
     */
    public void initialize() {
        searchByCB.setItems(setSearchTypes());
        searchByCB.getSelectionModel().selectLast();
        searchTextField.setDisable(true);
        appointmentsTablePlaceHolder = new PlaceHolderLabel("Select a customer to view their appointments");
        customerAppointmentsTable.setPlaceholder(appointmentsTablePlaceHolder);
        populateCustomersTable();
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerCityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bindSelectedTableItem();
    }
    public void bindSelectedTableItem(){
        customersTableView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                currentCustomerID = nv.getId();
                populateAppointmentsTable();
                deleteButton.disableProperty().bind(nv.activeMemberProperty().not());
                makeAppointmentButton.disableProperty().bind(nv.activeMemberProperty().not());
                editButton.disableProperty().bind(nv.activeMemberProperty().not());
                addressLabel.setText(nv.getAddress());
                cityLabel.setText(nv.getCity().getCityName() + ", ");
                stateLabel.setText(nv.getCity().getState().getStateCode() + ", ");
                postalCodeLabel.setText(nv.getPostalCode() + ", ");
                countryLabel.setText("U.S.A");
                phoneLabel.setText(nv.getPhoneNumber());
                emailLabel.setText(nv.getEmailAddress());
                FontAwesomeIconView fv = new FontAwesomeIconView();
                if(!nv.isActiveMember()){
                    statusLabel.setText("Inactive");
                    statusLabel.textFillProperty().setValue(Color.RED);
                    fv.setIcon(FontAwesomeIcon.TIMES_CIRCLE);
                    fv.setFill(Color.RED);
                } else{
                    statusLabel.setText("Active");
                    statusLabel.textFillProperty().setValue(Color.GREEN);
                    fv.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
                    fv.setFill(Color.GREEN);
                }
                statusLabel.setGraphic(fv);
                customerNameLabel.setText(nv.getName());
            }
        });
    }

    //Concurrent task running
    private void populateCustomersTable() {

        Task<List<Customer>> task = new Task<>() {
            @Override
            public ObservableList<Customer> call() throws Exception {
                return DBCustomers.getAllCustomers();
            }
        };
        task.setOnFailed(e-> {
            task.getException().printStackTrace();
        });
        task.setOnRunning(e-> {
            customersProgressIndicator = new ProgressIndicator();
            customersTableView.setPlaceholder(customersProgressIndicator);
        });
        task.setOnSucceeded(e-> {
            customersProgressIndicator.setProgress(1.0);
            customerList = (ObservableList<Customer>) task.getValue();
            customersTableView.setItems(customerList);
            customerTablePlaceHolder = new PlaceHolderLabel();
            customerTablePlaceHolder.setTextFill(Color.RED);
            customersTableView.setPlaceholder(customerTablePlaceHolder);
            //Creating a context menu for each row in the customers table
            customersTableView.setRowFactory(new Callback<TableView<Customer>, TableRow<Customer>>() {
                @Override
                public TableRow<Customer> call(TableView<Customer> tableView) {
                    final TableRow<Customer> tableRow = new TableRow<>();
                    final ContextMenu contextMenu = new ContextMenu();
                    final MenuItem deleteMenuItem = new MenuItem("Delete");
                    deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                handleDeleteButton(event);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
                    final MenuItem editMenuItem = new MenuItem("Edit");
                    editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            handleEditButton(event);
                        }
                    });
                    contextMenu.getItems().addAll(editMenuItem, separatorMenuItem, deleteMenuItem);
                    // Set context menu on row, but use a binding to make it only show for non-empty rows:
                    tableRow.contextMenuProperty().bind(
                            Bindings.when(tableRow.emptyProperty())
                                    .then((ContextMenu)null)
                                    .otherwise(contextMenu)
                    );
                    return tableRow ;
                }
            });
        });

        Main.getDbExecutor().execute(task);
    }
    private void populateAppointmentsTable() {
        Task<List<Appointment>> task = new Task<>() {
            @Override
            public ObservableList<Appointment> call() throws Exception {
                return DBAppointment.getAppointmentsForCustomer(currentCustomerID);
            }
        };
        task.setOnFailed(e-> {
            task.getException().printStackTrace();
        });
        task.setOnSucceeded(e-> {
            appointmentList = (ObservableList<Appointment>) task.getValue();
            customerAppointmentsTable.setItems(appointmentList);
            appointmentsTablePlaceHolder.setTextFill(Color.RED);
            appointmentsTablePlaceHolder.setText("No upcoming appointments");
            editCustomerAppointments.disableProperty().bind(Bindings.isEmpty(appointmentList));
        });
        Main.getDbExecutor().execute(task);
    }

    @FXML
    void handleEditButton(ActionEvent event) {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/CustomersEditView.fxml"));
            loader.load();
            parent = loader.getRoot();
            CustomersEditViewController cevController = loader.getController();
            currCustomer = customersTableView.getSelectionModel().getSelectedItem();
            cevController.fillCustomerTextFields(currCustomer);
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Edit Customer");
            stage.setScene(scene);
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.showAndWait();
            initialize();

        } catch (IOException e) {
              e.printStackTrace(System.out);
        }
        catch (NullPointerException ex) {
            AlertBoxes.noItemSelectedtoEdit(ownerStage);
        }
    }

    @FXML
    void handleAddButton(ActionEvent event) {
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/CustomersAddView.fxml"));
            loader.load();
            parent = loader.getRoot();
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Add Customer");
            stage.setScene(scene);
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.showAndWait();
            initialize();

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        } catch (NullPointerException ex) {
            AlertBoxes.noItemSelectedtoDelete(ownerStage);
        }

    }

    @FXML
    void handleDeleteButton(ActionEvent event) throws SQLException {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            Customer selCustomer = customersTableView.getSelectionModel().getSelectedItem();
            int currentSelId = selCustomer.getId();
            String currentSelName = selCustomer.getName();
            int numOfAppos = DBCustomers.getNumberOfCustomerAppointments(currentSelId);
            AlertBoxes.customerConfirmDelete(ownerStage, currentSelId, numOfAppos, currentSelName);
            initialize();
        } catch (NullPointerException e) {
            AlertBoxes.noItemSelectedtoDelete(ownerStage);
        }
    }

    public static ObservableList<String> setSearchTypes() {
        ObservableList<String> searchTypes = FXCollections.observableArrayList();
        searchTypes.add("All");
        searchTypes.add("Name");
        searchTypes.add("Address");
        searchTypes.add("Postal Code");
        searchTypes.add("Phone");
        searchTypes.add("City");
        searchTypes.add("None");
        return searchTypes;
    }

    @FXML
    void handleMakeAppointmentButton(ActionEvent event) {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/AppointmentsAddView.fxml"));
            loader.load();
            parent = loader.getRoot();

            AppointmentsAddViewController aavController = loader.getController();
            currCustomer = customersTableView.getSelectionModel().getSelectedItem();
            aavController.fillAddAppointmentCustomerField(currCustomer);
            System.out.println(currCustomer);
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Add Appointment");
            stage.setScene(scene);
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        } catch (NullPointerException ex) {

            AlertBoxes.noItemSelectedtoEdit(ownerStage);
        }
    }
    @FXML
    private void handleEditCustomerAppointments(ActionEvent event) {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/AppointmentsEditView.fxml"));
            loader.load();
            parent = loader.getRoot();
            AppointmentsEditViewController aevController = loader.getController();
            appointment = customerAppointmentsTable.getSelectionModel().getSelectedItem();
            aevController.fillEditAppointmentFields(appointment);
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Edit Appointment");
            stage.setScene(scene);
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.showAndWait();
            //Do Something
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        } catch (NullPointerException ex) {
            AlertBoxes.noAppointmentelectedtoEdit(ownerStage);
        }
    }

    @FXML
    private void handleSearchByCB(ActionEvent event) {
        String selectedFilter = searchByCB.getValue();
        if (selectedFilter != null) {

            switch (selectedFilter) {
                case "Name", "Address", "Postal Code", "Phone", "City", "All" -> {
                    searchTextField.setDisable(false);
                    searchTextField.requestFocus();
                    searchTextField.clear();
                }
                case "None" -> {
                    searchTextField.setDisable(true);
                    searchTextField.clear();
                }
                default -> {
                }
            }
        }
        initializeFilters();
    }


    private void initializeFilters() {
        // Wrap the ObservableList in a FilteredList (initially display all data).
        filteredData = new FilteredList<>(customerList, p -> true);
        // Set the filter Predicate whenever the filter changes.
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Predicate<Customer> searchFilter = cust -> {
                String selectedFilter = searchByCB.getValue();
                String lowerCaseFilter = newValue.toLowerCase();
                switch (selectedFilter) {
                    case "Name" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getName().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's Name.
                        } else {
                            customerTablePlaceHolder.setText("No matching name found containing: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    case "Address" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getAddress().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's address.
                        } else {
                            customerTablePlaceHolder.setText("No matching addresses found containing: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    case "Postal Code" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getPostalCode().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's postal code.
                        } else {
                            customerTablePlaceHolder.setText("No matching postal codes found containing: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    case "Phone" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getPhoneNumber().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's phone number.
                        } else {
                            customerTablePlaceHolder.setText("No matching phone numbers found containing: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    case "City" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getCity().getCityName().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's city.
                        } else {
                            customerTablePlaceHolder.setText("No matching cities found containing: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    case "All" -> {
                        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                            return true;
                        }
                        if (cust.getName().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's title
                        } else if (cust.getAddress().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's address
                        } else if (cust.getPostalCode().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's postal code
                        } else if (cust.getPhoneNumber().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's phone number
                        } else if (cust.getCity().getCityName().toLowerCase().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches customer's city
                        } else {
                            customerTablePlaceHolder.setText("No fields found starting with: ['" + newValue + "']");
                            return false; // Does not match.
                        }
                    }
                    default -> {
                    }
                }
                return false;
            };
            filteredData.setPredicate(searchFilter);
        });
        // Wrap the FilteredList in a SortedList.
        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(customersTableView.comparatorProperty());
        // Add sorted (and filtered) data to the table.
        customersTableView.setItems(sortedData);
    }

}
