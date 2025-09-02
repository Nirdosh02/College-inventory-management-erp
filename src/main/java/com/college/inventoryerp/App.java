package com.example.collegeinventoryerp;

//package com.college.inventory;

import com.example.collegeinventoryerp.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX Application class for College Inventory Management System
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Test database connection on startup
        testDatabaseConnection();

        // Load main FXML
        scene = new Scene(loadFXML("main"), 1400, 900);

        // Add CSS styling
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        // Configure stage
        stage.setTitle("College Inventory Management System");
        stage.setScene(scene);
        stage.setMaximized(true);
//        stage.setMinWidth(1200);
//        stage.setMinHeight(800);

        // Set application icon
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception e) {
            System.out.println("Application icon not found, using default.");
        }

        // Handle window closing
        stage.setOnCloseRequest(event -> {
            System.out.println("Application is closing...");
            DatabaseConnection.getInstance().closeConnection();
        });

        // Show the stage
        stage.show();

        System.out.println("College Inventory Management System started successfully!");
    }

    /**
     * Test database connection on application startup
     */
    private void testDatabaseConnection() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            if (dbConnection.testConnection()) {
                System.out.println("✅ Database connection successful!");
            } else {
                System.err.println("❌ Database connection failed!");
                showDatabaseConnectionError();
            }
        } catch (Exception e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            showDatabaseConnectionError();
        }
    }

    /**
     * Show database connection error dialog
     */
    private void showDatabaseConnectionError() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Unable to connect to the database");
            alert.setContentText("Please ensure that:\n\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'college_inventory_db' exists\n" +
                    "3. Username and password in DatabaseConnection.java are correct\n" +
                    "4. MySQL connector JAR is in the classpath\n\n" +
                    "Check console for detailed error messages.");
            alert.showAndWait();
        });
    }

    /**
     * Load FXML file
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Main method - entry point
     */
    public static void main(String[] args) {
        System.out.println("Starting College Inventory Management System...");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));

        launch(args);
    }
}

