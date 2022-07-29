/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.manuel.Main;
import org.manuel.utilities.UserLogger;
import org.manuel.uicontrols.SideBarControl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class MainViewController implements Initializable {

    @FXML
    private BorderPane mainPane;
    @FXML
    private ScrollPane centerScrollPane;
    /*-----------Top Bar----------*/
    @FXML
    private ButtonBar mainNavButtonBar;
    @FXML
    private Label titleLabel;
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private SplitMenuButton bookingsButton;
    @FXML
    private MenuItem calendarMenuItem;
    @FXML
    private MenuItem tableMenuItem;
    @FXML
    private Button customersButton;
    @FXML
    private Button reportsButton;
    @FXML
    private HBox navigationHBox;
    @FXML
    private ToggleButton sideMenuButton;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private FontAwesomeIconView fontawesomeIcon;

    private SideBarControl sideBarControl;
    /*-----------Side Bar End----------*/
    /*-----------Bottom Content----------*/
    @FXML
    private ProgressIndicator bottomProgressIndicator;

    private Main main;
    private final double expandedWidth = 205.0;
    SubScene subScene;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb  ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));

        sideBarControl = new SideBarControl(expandedWidth);
        subScene = new SubScene(sideBarControl, 205.0, 575.0);
        subScene.setLayoutY(50.0);
        subScene.setEffect(dropShadow);
        sideBarControl.setVisible(false);
        mainPane.getChildren().add(subScene);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsCalendarView"));
            }
        });
        titleLabel.setText("Manage Appointment (Calendar)");
    }

    @FXML
    void handleSideMenuButton(ActionEvent event) {
        // create an animation to hide sidebar.
        final Animation hideSidebar = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
            }

            protected void interpolate(double frac) {
                final double curWidth = expandedWidth * (1.0 - frac);
                sideBarControl.setPrefWidth(curWidth);
                sideBarControl.setTranslateX(-expandedWidth + curWidth);
                sideMenuButton.setDisable(true);
            }
        };

        hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sideBarControl.setVisible(false);
                fontawesomeIcon.setIcon(FontAwesomeIcon.ELLIPSIS_H);
                sideMenuButton.setDisable(false);
            }
        });

        // create an animation to show a sidebar.
        final Animation showSidebar = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
            }

            protected void interpolate(double frac) {
                final double curWidth = expandedWidth * frac;
                sideBarControl.setPrefWidth(curWidth);
                sideBarControl.setTranslateX(-expandedWidth + curWidth);
                sideMenuButton.setDisable(true);
            }
        };
        showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fontawesomeIcon.setIcon(FontAwesomeIcon.CHEVRON_LEFT);
                sideMenuButton.setDisable(false);
                sideBarControl.getSettingsButton().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        sideMenuButton.fire();
                        Main.getScrollPane().setContent(Main.loadSubViewFXML("SettingsView"));
                        titleLabel.setText("Settings");
                    }
                });
                sideBarControl.getLogoutButton().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Stage ownerStage = Main.getPrimaryStage();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Continue logout process?");
                        alert.setTitle("Confirm Logout");
                        alert.setHeaderText("Logging out");
                        alert.initOwner(ownerStage);
                        alert.showAndWait()
                                .filter(response -> response == ButtonType.OK)
                                .ifPresent((ButtonType response) -> {
                                    UserLogger.trackingLogout(Main.getUser().getUserName(), true);
                                    //System.out.println("User: " + "'" + Main.getUser().getUserName() + "'" + " logged out");
                                    try {
                                        MainViewController.this.main.setUser(null);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                    }
                });
            }
        });
        if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
            if (sideBarControl.isVisible()) {
                hideSidebar.play();
            } else {
                sideBarControl.setVisible(true);
                showSidebar.play();
            }
        }

    }

    @FXML
    void handleCustomersButton(ActionEvent event) {
        Main.getScrollPane().setContent(Main.loadSubViewFXML("CustomersMainView"));
        titleLabel.setText("Manage Customer");
    }

    @FXML
    void handleBookingsButton(ActionEvent event) {
        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsCalendarView"));
        titleLabel.setText("Manage Bookings (Calendar)");
    }

    @FXML
    void handleCalendarMenuItem(ActionEvent event) {
        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsCalendarView"));
        titleLabel.setText("Manage Bookings (Calendar)");
    }

    @FXML
    void handleTableMenuItem(ActionEvent event) {
        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsMainView"));
        titleLabel.setText("Manage Bookings (Table)");
    }

    @FXML
    void handleReportsButton(ActionEvent event) {
        Main.getScrollPane().setContent(Main.loadSubViewFXML("ReportsView"));
        titleLabel.setText("Report");
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public ScrollPane getCenterScrollPane() {
        return centerScrollPane;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }
}
