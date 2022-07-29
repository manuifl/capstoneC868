/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.manuel.Main;
import org.manuel.dao.DBAppointment;
import org.manuel.models.Appointment;
import org.manuel.uicontrols.AlertBoxes;
import org.manuel.uicontrols.CalendarDayButton;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class AppointmentsCalendarViewController implements Initializable {

    public Button previousMonthButton;
    public Button nextMonthButton;
    @FXML
    private Label calendarLabel;
    @FXML
    private StackPane calendarStackPane;
    @FXML
    private GridPane calendarHeaderGrid;
    @FXML
    private GridPane calendarButtonGrid;

    @FXML
    private Button viewOrEditAppointmentButton;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    @FXML
    private Label tableTitle;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, Timestamp> startCol;
    @FXML
    private TableColumn<Appointment, String> locationCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;

    public static Appointment selectedAppointment;

    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private YearMonth currentMonth;
    private YearMonth currentSelectedMonth;
    private LocalDate currentlySelectedDate;
    DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
    DateTimeFormatter monthDayYearFormat = DateTimeFormatter.ofPattern("LLL dd, yyyy", Locale.getDefault());
    Stage stage;
    Parent parent;
    Scene scene;
    ToggleGroup radioGroup = new ToggleGroup();
    Label placeHolder = new Label();
    ProgressIndicator progressIndicator;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();
        currentSelectedMonth = currentMonth;
        populateCalendar(currentSelectedMonth);
        placeHolder.setText("Select a date to view its contents");
        appointmentTable.setPlaceholder(placeHolder);
    }

    private CalendarDayButton createCalendarDayButton(LocalDate currentDate) {
        ObservableList<Appointment> filteredAppointments = appointments.stream()
                .filter(a -> a.getStartTime().toLocalDateTime().toLocalDate().equals(currentDate))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        CalendarDayButton calendarDayButton = new CalendarDayButton(currentDate, filteredAppointments);
        calendarDayButton.setToggleGroup(radioGroup);
        //Change Listener
        radioGroup.selectedToggleProperty().addListener((ov, oldValue, newValue) -> {
            appointmentTable.setItems(((CalendarDayButton) newValue).getAppointmentsList());
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            startCol.setCellFactory(new Callback<>() {
                @Override
                public TableCell<Appointment, Timestamp> call(TableColumn<Appointment, Timestamp> pastAppointments) {
                    return new TableCell<Appointment, Timestamp>() {
                        @Override
                        public void updateItem(final Timestamp item, final boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                setText(item.toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
                            }
                        }
                    };
                }
            });
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            tableTitle.setText(((CalendarDayButton) newValue).getDate().format(monthDayYearFormat));
            placeHolder.setText("No events scheduled");
            currentlySelectedDate = ((CalendarDayButton) newValue).getDate();
            viewOrEditAppointmentButton.disableProperty().bind(Bindings.isEmpty(appointmentTable.getItems()));
            deleteAppointmentButton.disableProperty().bind(Bindings.isEmpty(appointmentTable.getItems()));

        });
        return calendarDayButton;
    }

    private void populateCalendar(YearMonth startYearMonth) {
        LocalDate firstDate = startYearMonth.atDay(1);
        LocalDate lastDate = startYearMonth.atEndOfMonth().plusDays(1);

        LocalDateTime startDatetime = LocalDateTime.of(firstDate, LocalTime.MIDNIGHT);
        LocalDateTime endDatetime = LocalDateTime.of(lastDate, LocalTime.MIDNIGHT);

        Timestamp rangeStart = Timestamp.valueOf(startDatetime);
        Timestamp rangeEnd = Timestamp.valueOf(endDatetime);

        Task<List<Appointment>> task = new Task<>() {
            @Override
            public ObservableList<Appointment> call() throws Exception {
                return DBAppointment.getAppointmentsInRange(rangeStart, rangeEnd);
            }
        };
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });
        task.setOnRunning(e -> {
            progressIndicator = new ProgressIndicator();
            calendarStackPane.getChildren().add(progressIndicator);
        });
        task.setOnSucceeded(e -> {
            progressIndicator.setProgress(1.0);
            calendarStackPane.getChildren().remove(progressIndicator);
            resetCalendar();
            calendarLabel.setText(startYearMonth.format(yearMonthFormat));
            appointments = (ObservableList<Appointment>) task.getValue();
            int monthlyOffset = getMonthOffset();
            for (LocalDate date = firstDate; date.isBefore(lastDate); date = date.plusDays(1)) {
                int dayOfMonth = date.getDayOfMonth();
                int dayOfMonthMinusOffset = dayOfMonth - monthlyOffset;
                CalendarDayButton calendarDayButton = createCalendarDayButton(date);
                calendarButtonGrid.add(calendarDayButton, dayOfMonthMinusOffset % 7, dayOfMonthMinusOffset / 7);
                if(calendarDayButton.getDate().equals(LocalDate.now())){
                    calendarDayButton.setSelected(true);
                }
            }
        });
        Main.getDbExecutor().execute(task);
    }

    // Reset calendar screen.
    public void resetCalendar() {
        selectedAppointment = null;
        calendarButtonGrid.getChildren().clear();
        appointmentTable.getItems().clear();
        placeHolder.setText("Select a date to view its contents");
        tableTitle.setText("Select A Day");
    }

    /**
     * Get span between first day of month and Sunday.
     * Used to accommodate the calendar day buttons under the appropriate day of the week
     *
     * @return int
     */
    private int getMonthOffset() {
        LocalDate firstDay = currentMonth.atDay(1);
        DayOfWeek dayName = firstDay.getDayOfWeek();
        int dayNameValue = dayName.getValue();
        int offset = (dayNameValue - 1) % 7;
        LocalDate firstSunday = firstDay.minusDays(offset);
        Period span = Period.between(firstDay, firstSunday);
        return span.getDays();
    }

    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentMonth = currentMonth.plusMonths(1);
        currentSelectedMonth = currentMonth;
        populateCalendar(currentSelectedMonth);
    }

    @FXML
    private void handlePreviousMonth(ActionEvent event) {
        currentMonth = currentMonth.minusMonths(1);
        currentSelectedMonth = currentMonth;
        populateCalendar(currentSelectedMonth);
    }

    @FXML
    private void handleViewOrEditAppointmentBT(ActionEvent event) {
        Stage ownerStage = Main.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/external/AppointmentsEditView.fxml"));
            loader.load();
            parent = loader.getRoot();
            AppointmentsEditViewController aevController = loader.getController();
            selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            aevController.fillEditAppointmentFields(selectedAppointment);
            scene = new Scene(parent, 565, 600);
            stage = new Stage();
            stage.setTitle("Edit Appointment");
            stage.setScene(scene);

            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.showAndWait();
            populateCalendar(currentSelectedMonth);
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        } catch (NullPointerException ex) {

            AlertBoxes.noAppointmentelectedtoEdit(ownerStage);
        }
    }

    @FXML
    private void handleAddAppointmentBT(ActionEvent event) throws IOException {
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
        stage.setResizable(false);
        stage.showAndWait();
        populateCalendar(currentSelectedMonth);
        currentlySelectedDate = null;
    }

    @FXML
    private void handleDeleteAppointmentBT(ActionEvent event) {
        Stage ownerStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try {
            selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            String title = selectedAppointment.getTitle();
            String idString = String.valueOf(selectedAppointment.getId());
            String type = selectedAppointment.getType();
            AlertBoxes.appointmentConfirmDelete(ownerStage, idString, title, type, selectedAppointment.getId());
            populateCalendar(currentSelectedMonth);
        } catch (NullPointerException ex) {
            AlertBoxes.noItemSelectedtoDelete(ownerStage);
        }
    }

    @Override
    public String toString() {
        return "AppointmentsCalendarViewController";
    }
}
