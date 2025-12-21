package com.hotel;

import com.hotel.util.NavigationUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class HotelManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setMinWidth(1600);
        primaryStage.setMinHeight(900);

        // Set up navigation utility
        NavigationUtil.setPrimaryStage(primaryStage);

        // Load dashboard (handles scene creation and primaryStage.show())
        NavigationUtil.loadDashboard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
