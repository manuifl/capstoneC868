package org.manuel.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.manuel.Main;
import org.manuel.dao.DBUsers;
import org.manuel.models.User;
import org.manuel.utilities.UserLogger;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML
    private Label loginTitleLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Label statusMessageLabel;

    @FXML
    private Label zoneIdLabel;

    @FXML
    private StackPane loginStackPane;

    @FXML
    private Button loginButton;

    @FXML
    private Button closeButton;

    private Main main;

    Stage stage;
    ZoneId zoneId = ZoneId.systemDefault();
    Locale locale = Locale.getDefault();
    private static final String FILENAME = "resources/login";
    ResourceBundle resourceBundle;


    /**
     * Initializes LoginViewController
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        userNameTextField.setText("test");
//        passwordTextField.setText("testPass");
        statusMessageLabel.setTextFill(Color.web("#ff0000"));
        if(Main.getConn() == null){
            statusMessageLabel.setText("Connection Failed: Please check your internet connection");
            loginButton.setDisable(true);
        }
        try {
            rb = ResourceBundle.getBundle("org/manuel/login", locale);
            //Set internationalization variables
            loginTitleLabel.setText(rb.getString("title"));
            userNameTextField.setPromptText(rb.getString("usernamePrompt"));
            passwordTextField.setPromptText(rb.getString("passwordPrompt"));
            loginButton.setText(rb.getString("signIn"));
            closeButton.setText(rb.getString("close"));
            String zoneIdInfo = zoneId.toString();
            String localeInfo = locale.toString();
            zoneIdLabel.setText(rb.getString("zoneInfo") + " " + zoneIdInfo);

        } catch (MissingResourceException e) {
            e.printStackTrace(System.out);
        }
    }

    @FXML
    void handleCloseButton(ActionEvent event) {
        stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    void handleLoginButton(ActionEvent event) throws IOException {
        String username = userNameTextField.getText();
        String password = passwordTextField.getText();
        String hash;
        resourceBundle = ResourceBundle.getBundle("org/manuel/login", locale);
        if (username.isEmpty() || password.isEmpty()) {
            statusMessageLabel.setText(resourceBundle.getString("empty"));
        } else {
            if (DBUsers.validUserName(username)) {
                hash = DBUsers.getPasswordHash(password, DBUsers.getUserSalt(userNameTextField.getText()));
                User user = DBUsers.secureLogin(username, hash);
                if (user != null) {
                    UserLogger.trackingLogin(username, true);
                    this.main.setUser(user);
                } else {
                    UserLogger.trackingLogin(username, false);
                    statusMessageLabel.setText(resourceBundle.getString("incorrect"));
                }
            } else{
                UserLogger.trackingLogin(username, false);
                statusMessageLabel.setText(resourceBundle.getString("incorrect"));
            }
        }
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
