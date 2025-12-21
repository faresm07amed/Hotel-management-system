package com.hotel.controller;

import com.hotel.util.NavigationUtil;
import javafx.fxml.FXML;

public class AboutController {

    @FXML
    public void goToDashboard() {
        NavigationUtil.loadDashboard();
    }

    @FXML
    public void goToHome() {
        NavigationUtil.loadHome();
    }

    @FXML
    public void goToGuests() {
        NavigationUtil.loadGuests();
    }

    @FXML
    public void goToRooms() {
        NavigationUtil.loadRooms();
    }

    @FXML
    public void goToReservations() {
        NavigationUtil.loadReservations();
    }

    @FXML
    public void goToPayments() {
        NavigationUtil.loadPayments();
    }

    @FXML
    public void goToServices() {
        NavigationUtil.loadServices();
    }

    @FXML
    public void goToAbout() {
        NavigationUtil.loadAbout();
    }
}
