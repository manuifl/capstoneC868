package org.manuel.uicontrols;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.manuel.Main;
import org.manuel.utilities.UserLogger;

import java.io.IOException;
import java.sql.SQLException;

/** Animates a node on and off-screen to the left. */
public class SideBarControl extends VBox {
//    @FXML
//    private SplitMenuButton appointmentsButton;
//
//    @FXML
//    private MenuItem calendarMenuItem;
//
//    @FXML
//    private MenuItem tableMenuItem;
//
//    @FXML
//    private Button customersButton;
//
//    @FXML
//    private ImageView customersImageView;
//
//    @FXML
//    private Button reportButton;
//
//    @FXML
//    private ImageView reportImageView;

    @FXML
    private Label userNameLabel;
    @FXML
    private Button logoutButton;

    @FXML
    private Button settingsButton;

    private Main main;
//    @FXML
//    private ImageView settingsImageView;



    /**
     * sets SideBarControl as both the root and controller of SideBar.fxml
     */
    public SideBarControl(final double expandedWidth) {
        getStyleClass().add("side-menu-bar");
        this.setPrefWidth(expandedWidth);
        this.setMinWidth(0);
        setAlignment(Pos.CENTER);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/components/SideBar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        userNameLabel.setText(Main.getUser().getUserName());
    }



//    public void initialize(){
//    }
//    @FXML
//    public void handleAppointmentsButton(ActionEvent event) {
//        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsCalendarView"));
//    }
//
//    @FXML
//    public void handleCalendarMenuItem(ActionEvent event) {
//        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsCalendarView"));
//    }
//
//    @FXML
//    public void handleTableMenuItem(ActionEvent event) {
//        Main.getScrollPane().setContent(Main.loadSubViewFXML("AppointmentsMainView"));
//    }
//
//    @FXML
//    public void handleCustomersButton(ActionEvent event) {
//        Main.getScrollPane().setContent(Main.loadSubViewFXML("CustomersMainView"));
//    }
//
//    @FXML
//    public void handleReportButton(ActionEvent event) {
//        Main.getScrollPane().setContent(Main.loadSubViewFXML("ReportsView"));
//    }

    @FXML
    void handleLogoutButton(ActionEvent event) throws SQLException, IOException {
        Stage ownerStage = Main.getPrimaryStage();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Continue logout process?");
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logging out");
        alert.initOwner(ownerStage);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent((ButtonType response) -> {
                    UserLogger.trackingLogout(Main.getUser().getUserName(), true);
                    System.out.println("User: " + "'" + Main.getUser().getUserName() + "'" + " logged out");
                    //CurrentUserLogger.clearCurrentUserName();
                    try {
                        this.main.setUser(null);
//                        Main.setScene("LoginView");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }



    public void setMain(Main main) {
        this.main = main;
    }

    public Label getUserNameLabel() {

        return userNameLabel;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}