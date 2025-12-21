package com.hotel.controller;

import com.hotel.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HomeController {

    @FXML
    private VBox faq1, faq2, faq3;
    @FXML
    private Label faqIcon1, faqIcon2, faqIcon3;

    @FXML
    public void initialize() {
        // Any initialization logic for the home page
    }

    @FXML
    private void toggleFaq1() {
        toggleFaq(faq1, faqIcon1);
    }

    @FXML
    private void toggleFaq2() {
        toggleFaq(faq2, faqIcon2);
    }

    @FXML
    private void toggleFaq3() {
        toggleFaq(faq3, faqIcon3);
    }

    private void toggleFaq(VBox faqBody, Label icon) {
        boolean isVisible = faqBody.isVisible();
        faqBody.setVisible(!isVisible);
        faqBody.setManaged(!isVisible);
        icon.setText(!isVisible ? "−" : "+");
    }

    @FXML
    private void goToDashboard() {
        NavigationUtil.loadDashboard();
    }

    @FXML
    private void goToGuests() {
        NavigationUtil.loadGuests();
    }

    @FXML
    private void goToRooms() {
        NavigationUtil.loadRooms();
    }

    @FXML
    private void goToReservations() {
        NavigationUtil.loadReservations();
    }

    @FXML
    private void goToPayments() {
        NavigationUtil.loadPayments();
    }

    @FXML
    private void goToServices() {
        NavigationUtil.loadServices();
    }

    @FXML
    private void goToHome() {
        NavigationUtil.loadHome();
    }
}
