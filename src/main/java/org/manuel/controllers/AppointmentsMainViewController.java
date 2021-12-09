/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.models.Appointment;
import org.manuel.uicontrols.AlertBoxes;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
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
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.manuel.uicontrols.PlaceHolderLabel;
import org.manuel.utilities.formatters.DateStringConverter;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class AppointmentsMainViewController {


    /*--FILTER MENU--*/
    @FXML
    private MenuButton filterMenuButton;
    @FXML
    private ToggleGroup filterToggleGroup;
    //Date Filter
    @FXML
    private RadioMenuItem noneRadioItem;
    @FXML
    private RadioMenuItem dayRadioItem;
    @FXML
    private RadioMenuItem weekRadioItem;
    @FXML
    private RadioMenuItem monthRadioItem;
    @FXML
    private DatePicker datePickerFilter;
    @FXML
    private TextField textFieldFilter;
    @FXML
    private HBox hBox;
    @FXML
    private TableView<Appointment> appointmentsTableView;
    @FXML
    private TableColumn<Appointment, Integer> idCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> descCol;
    @FXML
    private TableColumn<Appointment, String> locationCol;
    @FXML
    private TableColumn<Appointment, Timestamp> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, String> customerCol;
    @FXML
    private TableColumn<Appointment, LocalDate> statusCol;
    @FXML
    private Label placeHolderLabel;
    @FXML
    private Button viewOrEditAppointmentButton;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    //Labels
    @FXML
    private Label statusLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;
    @FXML
    private TitledPane customerTitledPane;
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
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label createdByLabel;
    @FXML
    private Label createDateLabel;
    @FXML
    private Label lastUpdatedByLabel;
    @FXML
    private Label lastUpdatedLabel;

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private FilteredList<Appointment> filteredData;

    public static Appointment currAppointment;
    public static int id;
    private ProgressIndicator progressIndicator;

    Stage stage;
    Parent parent;
    Scene scene;



    /**
     * Initializes the controller class.
     */
    public void initialize() {
        //FILTER
        dayRadioItem.setUserData("Day");
        weekRadioItem.setUserData("Week");
        monthRadioItem.setUserData("Month");
        noneRadioItem.setUserData("None");
        //Default Values
        filterMenuButton.setText("None");
        noneRadioItem.setSelected(true);
        datePickerFilter.setDisable(true);
        toggleListener();
        setTableColumnCells();
        populateAppointmentsTable();
        bindSelectedTableItem();
    }

    private void setTableColumnCells() {
        statusCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, LocalDate> call(TableColumn<Appointment, LocalDate> pastAppointments) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(final LocalDate item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            FontAwesomeIconView fv = new FontAwesomeIconView();
                            if (item.isBefore(LocalDate.now())) {
                                fv.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
                                fv.setFill(Color.GREY);
                            } else {
                                fv.setIcon(FontAwesomeIcon.CLOCK_ALT);
                                fv.setFill(Color.BLACK);
                            }
                            setGraphic(fv);
                        }
                    }
                };
            }
        });
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, Timestamp> call(TableColumn<Appointment, Timestamp> pastAppointments) {
                return new TableCell<Appointment, Timestamp>() {
                    @Override
                    public void updateItem(final Timestamp item, final boolean empty) {
                        super.updateItem(item, empty);
                        this.getTableRow().getStyleClass().remove("past-appointments-row");
                        this.getTableRow().getStyleClass().remove("future-appointments-row");
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.toString());
                            this.getTableRow().getStyleClass().add(item.toLocalDateTime().isBefore(LocalDateTime.now()) ? "past-appointments-row" : "future-appointments-row");
                        }
                    }
                };
            }
        });
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
    }

    public void bindSelectedTableItem(){
        appointmentsTableView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                id = nv.getId();
                if(nv.getStartTime().toLocalDateTime().isBefore(LocalDateTime.now())){
                    statusLabel.setText("Completed");
                    viewOrEditAppointmentButton.setDisable(true);
                } else {
                    if (nv.getStartTime().toLocalDateTime().isEqual(LocalDateTime.now())){
                    statusLabel.setText("Occurring Now ");

                    } else if (nv.getStartTime().toLocalDateTime().isAfter(LocalDateTime.now())){
                    statusLabel.setText("Pending");
                    }
                    viewOrEditAppointmentButton.setDisable(false);
                }
                titleLabel.setText(nv.getTitle());
                descriptionLabel.setText(nv.getDescription());
                locationLabel.setText(nv.getLocation().getLocationName());
                startLabel.setText(nv.getStartTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
                endLabel.setText(nv.getEndTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
                //Contact Info
                addressLabel.setText(nv.getCustomer().getAddress());
                cityLabel.setText(nv.getCustomer().getCity().getCityName() + ", ");
                stateLabel.setText(nv.getCustomer().getCity().getState().getStateCode() + ", ");
                postalCodeLabel.setText(nv.getCustomer().getPostalCode() + ", ");
                countryLabel.setText("U.S.A");
                phoneLabel.setText(nv.getCustomer().getPhoneNumber());
                emailLabel.setText(nv.getCustomer().getEmailAddress());
                //Account Info
                customerTitledPane.setText(nv.getCustomer().getName());
                createDateLabel.setText(nv.getCreateDate().toString());
                createdByLabel.setText(nv.getCreatedBy());
                lastUpdatedLabel.setText(nv.getLastUpdate().toString());
                lastUpdatedByLabel.setText(nv.getLastUpdatedBy());
            }
        });
    }

    /**
     * Concurrent method runs outside FXThread
     */
    private void populateAppointmentsTable() {
        Task<List<Appointment>> task = new Task<>() {
            @Override
            public ObservableList<Appointment> call() throws Exception {
                return DBAppointment.getAllAppointments();
            }
        };
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });
        task.setOnRunning(e -> {
            progressIndicator = new ProgressIndicator();
            appointmentsTableView.setPlaceholder(progressIndicator);
        });
        task.setOnSucceeded(e -> {
            progressIndicator.setProgress(1.0);
            appointmentList = (ObservableList<Appointment>) task.getValue();
            appointmentsTableView.setItems(appointmentList);
            placeHolderLabel = new PlaceHolderLabel();
            appointmentsTableView.setPlaceholder(placeHolderLabel);
            initializeFilters();
        });
        Main.getDbExecutor().execute(task);
    }

    private void initializeFilters() {
        // Wrap the ObservableList in a FilteredList (initially display all data).
        filteredData = new FilteredList<>(appointmentList, p -> true);
        // Set the filter Predicate whenever the filter changes.
        textFieldFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            Predicate<Appointment> filter = filters(newValue, datePickerFilter.getEditor().getText());
            filteredData.setPredicate(filter);
        });
        datePickerFilter.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Predicate<Appointment> filter = filters(textFieldFilter.getText(), newValue);
            filteredData.setPredicate(filter);
        });
        // Wrap the FilteredList in a SortedList.
        SortedList<Appointment> sortedData = new SortedList<>(filteredData);
        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(appointmentsTableView.comparatorProperty());
        // Add sorted (and filtered) data to the table.
        appointmentsTableView.setItems(sortedData);
    }

    private Predicate<Appointment> filters(String search, String date) {
        placeHolderLabel.setTextFill(Color.RED);

        Predicate<Appointment> searchFilter = appo -> {
            String lowerCaseFilter = search.trim().toLowerCase();
            if (lowerCaseFilter.isEmpty() || lowerCaseFilter.isBlank()) {
                return true;
            }
            if (appo.getTitle().toLowerCase().startsWith(lowerCaseFilter)) {
                return true; // Filter matches appointment title.
            } else if(appo.getCustomer().getName().startsWith(lowerCaseFilter)){
                return true; // Filter matches appointment customerName.
            } else {
                placeHolderLabel.setText("No matches found for filter and search criteria");
                return false; // Does not match.
            }
        };

        Predicate<Appointment> dateFilter = appo -> {
            LocalDate ld = datePickerFilter.getValue();
            DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
            DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("EEEE , MMMM d");
            boolean emptyDate = date == null || date.equals("null") || date.isEmpty() || date.isBlank();

            String selectedFilter = filterToggleGroup.getSelectedToggle().getUserData().toString();
            switch (selectedFilter) {
                // Appointment with a startDate equal to the datePicker's selected day
                case "Day" -> {
                    if (emptyDate) {
                        return true;
                    }
                    if (appo.getStartDate().equals(LocalDate.parse(date))) {
                        return true; // Matches
                    } else {
                        placeHolderLabel.setText("No matches found for filter and search criteria");
                        return false; // Does not match
                    }
                }
                // Appointment with a startDate occurring in the week of the datePicker's selected day
                case "Week" -> {
                    String year = ld.format(yearFormatter);
                    TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
                    LocalDate firstDayOfWeek = ld.with(fieldUS, 1);
                    LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);
                    if (emptyDate) {
                        return true;
                    }
                    if (appo.getStartDate().isAfter(firstDayOfWeek.minusDays(1)) && appo.getStartDate().isBefore(lastDayOfWeek.plusDays(1))) {
                        return true; // Matches
                    } else {
                        placeHolderLabel.setText("No matches found for filter and search criteria");
                        return false; // Does not match
                    }
                }
                // Appointment with a startDate occurring in the month of the datePicker's selected day
                case "Month" -> {
                    String year = ld.format(yearFormatter);
                    String month = ld.format(DateTimeFormatter.ofPattern("M"));
                    String monthName = ld.getMonth().toString();
                    YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
                    LocalDate firstDayOfMonth = yearMonth.atDay(1);
                    LocalDate lastDayOfMonth = yearMonth.atEndOfMonth().plusDays(1);
                    if (emptyDate) {
                        return true;
                    }
                    if (appo.getStartDate().isAfter(firstDayOfMonth) && appo.getStartDate().isBefore(lastDayOfMonth)) {
                        return true; // Matches.
                    } else {
                        placeHolderLabel.setText("No matches found for filter and search criteria");
                        return false; // Does not match
                    }
                }
                // Disables filtering by date showing all appointments and relies only on text filter
                case "None" -> {
                    if (emptyDate) {
                        return true;
                    } else {
                        placeHolderLabel.setText("No matches found for filter and search criteria");
                        return false; // Does not match.
                    }
                }
                default -> {
                }
            }
            return false;
        };
        return searchFilter.and(dateFilter);
    }

    @FXML
    private void handleViewOrEditAppointmentBT(ActionEvent event) {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/AppointmentsEditView.fxml"));
            loader.load();
            parent = loader.getRoot();
            AppointmentsEditViewController aevController = loader.getController();
            currAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();
            aevController.fillEditAppointmentFields(currAppointment);
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Edit Appointment");
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
            AlertBoxes.noItemSelectedtoEdit(ownerStage);
        }
    }

    @FXML
    private void handleAddAppointmentBT(ActionEvent event) throws IOException, SQLException {
        Stage ownerStage = Main.getPrimaryStage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/AppointmentsAddView.fxml"));
        loader.load();
        parent = loader.getRoot();
        scene = new Scene(parent, 565, 600);
        stage = new Stage();
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.initOwner(ownerStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
        stage.showAndWait();
        initialize();
    }

    @FXML
    void handleDeleteAppointmentBT(ActionEvent event) {
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try {
            Appointment selAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();
            int currentSelId = selAppointment.getId();
            String currentSelAppointment = selAppointment.getTitle();
            String currentSelIdString = String.valueOf(currentSelId);
            String currentSelType = selAppointment.getType();
            AlertBoxes.appointmentConfirmDelete(ownerStage, currentSelIdString, currentSelAppointment, currentSelType, currentSelId);
            initialize();
        } catch (NullPointerException ex) {
            AlertBoxes.noItemSelectedtoDelete(ownerStage);
        }
    }

    public void toggleListener(){
        filterToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            String selected = filterToggleGroup.getSelectedToggle().getUserData().toString();
            if (new_toggle != null) {
                filterMenuButton.setText(selected);
                switch (selected) {
                    case "Day", "Month", "Week" -> {
                        if (selected.equalsIgnoreCase("Day")) {
                            datePickerFilter.setConverter(new DateStringConverter(DateTimeFormatter.ofPattern("yyyy-MM-dd"), datePickerFilter.getEditor().getText(), datePickerFilter.getValue()));
                            datePickerFilter.setShowWeekNumbers(false);
                        } else if (selected.equalsIgnoreCase("Week")) {
                            datePickerFilter.setConverter(new DateStringConverter(DateTimeFormatter.ofPattern("ww-yyyy"), datePickerFilter.getEditor().getText(), datePickerFilter.getValue()));
                            datePickerFilter.setShowWeekNumbers(true);
                        } else {
                            datePickerFilter.setConverter(new DateStringConverter(DateTimeFormatter.ofPattern("MMM-yyyy"), datePickerFilter.getEditor().getText(), datePickerFilter.getValue()));
                            datePickerFilter.setShowWeekNumbers(false);
                        }
                        datePickerFilter.getEditor().clear();
                        datePickerFilter.setValue(null);
                        datePickerFilter.setDisable(false);
                        datePickerFilter.show();
                    }
                    case "None" -> {
                        datePickerFilter.getEditor().clear();
                        datePickerFilter.setDisable(true);
                    }
                    default -> {
                    }
                }
            }
        });
    }

}

