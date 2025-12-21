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

            if (primaryStage.getScene() == null) {
                // First time loading: create scene with default dimensions
                Scene scene = new Scene(root, 1200, 800);
                primaryStage.setScene(scene);
            } else {
                // Subsequent navigation: update root to maintain current window size
                primaryStage.getScene().setRoot(root);
            }

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
