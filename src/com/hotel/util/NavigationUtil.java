package com.hotel.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void loadView(String fxmlPath) {
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            System.out.println("FXML loaded successfully");

            Scene scene = new Scene(root);

            // DISABLED: CSS loading causes StackOverflowError due to circular references
            // Try to load CSS, but don't fail if it's not found
            /*
             * try {
             * String cssPath = "/com/hotel/view/styles.css";
             * java.net.URL cssUrl = NavigationUtil.class.getResource(cssPath);
             * if (cssUrl != null) {
             * scene.getStylesheets().add(cssUrl.toExternalForm());
             * System.out.println("CSS loaded successfully");
             * } else {
             * System.err.println("WARNING: CSS file not found at: " + cssPath);
             * }
             * } catch (Exception cssEx) {
             * System.err.println("WARNING: Failed to load CSS: " + cssEx.getMessage());
             * // Continue without CSS
             * }
             */

            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("View displayed successfully");
        } catch (IOException e) {
            System.err.println("ERROR loading view: " + fxmlPath);
            e.printStackTrace();
            AlertUtil.showError("Navigation Error",
                    "Failed to load view: " + fxmlPath,
                    e.getMessage());
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadDashboard() {
        loadView("/com/hotel/view/dashboard.fxml");
    }

    public static void loadGuests() {
        loadView("/com/hotel/view/guests.fxml");
    }

    public static void loadRooms() {
        loadView("/com/hotel/view/rooms.fxml");
    }

    public static void loadReservations() {
        loadView("/com/hotel/view/reservations.fxml");
    }

    public static void loadPayments() {
        loadView("/com/hotel/view/payments.fxml");
    }

    public static void loadServices() {
        loadView("/com/hotel/view/services.fxml");
    }
}
