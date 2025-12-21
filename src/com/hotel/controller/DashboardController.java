package com.hotel.controller;

import com.hotel.dao.*;
import com.hotel.model.RoomStatus;
import com.hotel.model.ReservationStatus;
import com.hotel.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class DashboardController {
    @FXML
    private Label totalGuestsLabel;
    @FXML
    private Label availableRoomsLabel;
    @FXML
    private Label activeReservationsLabel;
    @FXML
    private Label totalRevenueLabel;

    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;
    private PaymentDAO paymentDAO;

    public DashboardController() {
        System.out.println("DashboardController constructor starting...");
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        reservationDAO = new ReservationDAO();
        paymentDAO = new PaymentDAO();
        System.out.println("DashboardController constructor completed");
    }

    @FXML
    public void initialize() {
        System.out.println("DashboardController.initialize() called");
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Total Guests
            int totalGuests = guestDAO.getAllGuests().size();
            totalGuestsLabel.setText(String.valueOf(totalGuests));

            // Available Rooms
            int availableRooms = roomDAO.getRoomsByStatus(RoomStatus.AVAILABLE).size();
            availableRoomsLabel.setText(String.valueOf(availableRooms));

            // Active Reservations (Confirmed + Checked In)
            int confirmedReservations = reservationDAO.getReservationsByStatus(ReservationStatus.CONFIRMED).size();
            int checkedInReservations = reservationDAO.getReservationsByStatus(ReservationStatus.CHECKED_IN).size();
            int activeReservations = confirmedReservations + checkedInReservations;
            activeReservationsLabel.setText(String.valueOf(activeReservations));

            // Total Revenue
            double totalRevenue = paymentDAO.getTotalRevenue();
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        } catch (Exception e) {
            System.err.println("Error loading dashboard statistics: " + e.getMessage());
            e.printStackTrace();

            // Set default values if database connection fails
            totalGuestsLabel.setText("0");
            availableRoomsLabel.setText("0");
            activeReservationsLabel.setText("0");
            totalRevenueLabel.setText("$0.00");
        }
    }

    @FXML
    private void refreshDashboard() {
        loadStatistics();
    }

    @FXML
    private void goToDashboard() {
        NavigationUtil.loadDashboard();
    }

    @FXML
    private void goToHome() {
        NavigationUtil.loadHome();
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
    public void goToServices() {
        NavigationUtil.loadServices();
    }

    @FXML
    public void goToAbout() {
        NavigationUtil.loadAbout();
    }
}