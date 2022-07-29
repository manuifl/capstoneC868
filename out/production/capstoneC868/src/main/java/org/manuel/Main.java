/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.manuel.controllers.LoginViewController;
import org.manuel.controllers.MainViewController;
import org.manuel.dao.DBConnection;
import org.manuel.dao.ThreadFactoryImpl;
import org.manuel.models.User;
import org.manuel.utilities.UserLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class of application
 *
 * @author Manuel Fuentes
 */
public class Main extends Application {


    private static Scene scene;
    private static Connection conn;
    private static Stage primaryStage;
    private static BorderPane borderPane;
    private static ScrollPane scrollPane;

    private static User user;
    private static final LocalTime openTime = LocalTime.of(8, 0, 0);
    private static final LocalTime closeTime = LocalTime.of(22, 0, 0);

//    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryImpl());
    private static ExecutorService dbExecutor;


    @Override
    public void init() {
        //For concurrency: Create executor that uses daemon threads:
        dbExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread t = new Thread (runnable);
            t.setName("DBTread");
            t.setDaemon(true);
            return t;
        });

    }
    /*-----------------------------------------------------------------------*/

    public static Parent loadMainViewFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Parent loadSubViewFXML(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/subviews/" + fxml + ".fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
    /*-----------------------------------------------------------------------*/

    /**
     * Application start
     * @param primaryStage Stage
     * @throws Exception Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Main.scene = new Scene(loadMainViewFXML("LoginView"));
        primaryStage.setScene(scene);
        Main.primaryStage.setTitle("Co-Working Lots");
        File stageIconFile = new File("src/main/resources/org/manuel/images/appLogo.png");
        Image stageIcon = new Image(stageIconFile.toURI().toString());
        Main.primaryStage.getIcons().add(stageIcon);
        showLoginView();
    }
    /*-----------------------------------------------------------------------*/
    /**
     * Initializes the root layout.
     */
    public void initBorderPane() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/MainView.fxml"));
            loader.load();
            MainViewController controller = loader.getController();
            // Show the scene containing the root layout.
            borderPane = controller.getMainPane();
            scrollPane = controller.getCenterScrollPane();
            borderPane.setCenter(scrollPane);
            controller.setMain(Main.this);
            getPrimaryStage().getScene().setRoot(borderPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the content inside the borderpane.
     */
    public static void loadBPSubView(String fxml) {
            scrollPane.setContent(loadSubViewFXML(fxml));
    }
    public void showMainView(String fxml) {
        initBorderPane();
        loadBPSubView(fxml);
    }


    /*-----------------------------------------------------------------------*/
    public void showLoginView() {
        // Load Login View
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/LoginView.fxml"));
            Parent parent = loader.load();
            getPrimaryStage().getScene().setRoot(parent);
            LoginViewController controller = loader.getController();
            controller.setMain(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*-----------------------------------------------------------------------*/

    // Establish logged-in user.
    public void setUser(User loggedInUser) throws IOException {
        user = loggedInUser;
        if (user != null) {
            initBorderPane();
        } else {
            showLoginView();
        }
    }
    /**
     * Returns the main stage.
     *
     * @return Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns border pane center scrollPane.
     *
     * @return ScrollPane
     */
    public static ScrollPane getScrollPane() {
        return scrollPane;
    }

    public static BorderPane getBorderPane() {
        return borderPane;
    }

    public static User getUser() {
        return user;
    }

    public static Executor getDbExecutor() {
        return dbExecutor;
    }

    public static LocalTime getCloseTime() {
        return closeTime;
    }

    public static LocalTime getOpenTime() {
        return openTime;
    }

    public static Connection getConn() {
        return conn;
    }

    /*-----------------------------------------------------------------------*/

    /**
     * Main function
     * @param args
     */
    public static void main(String[] args) {

        //Testing Internationalization
        //Locale.setDefault(Locale.FRANCE);
        //Testing Timezones
        TimeZone tz1 = TimeZone.getTimeZone("America/New_York");
        //TimeZone tz2 = TimeZone.getTimeZone("Etc/GMT+8");
        TimeZone.setDefault(tz1);
        conn = DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }

    @Override
    public void stop() {
        if (user != null) {
            UserLogger.trackingLogout(user.getUserName(), true);
            System.out.println("User: " + user + " logged out");
        }
    }


}

