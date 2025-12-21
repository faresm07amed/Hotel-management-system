package com.hotel.controller;

import com.hotel.dao.GuestDAO;
import com.hotel.model.Guest;
import com.hotel.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class GuestController {
    @FXML
    private TableView<Guest> guestsTable;
    @FXML
    private TableColumn<Guest, Integer> idColumn;
    @FXML
    private TableColumn<Guest, String> firstNameColumn;
    @FXML
    private TableColumn<Guest, String> lastNameColumn;
    @FXML
    private TableColumn<Guest, String> emailColumn;
    @FXML
    private TableColumn<Guest, String> phoneColumn;
    @FXML
    private TableColumn<Guest, String> idNumberColumn;
    @FXML
    private TableColumn<Guest, String> addressColumn;

    @FXML
    private TextField searchField;
    @FXML
    private VBox guestFormPanel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField idNumberField;
    @FXML
    private TextArea addressArea;

    private GuestDAO guestDAO = new GuestDAO();
    private Guest selectedGuest = null;

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        idNumberColumn.setCellValueFactory(cellData -> cellData.getValue().idNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        try {
            refreshGuests();
        } catch (Exception e) {
            System.err.println("Warning: Could not load guests from database: " + e.getMessage());
            // View will load anyway, just with empty table
        }
    }

    @FXML
    private void refreshGuests() {
        guestsTable.setItems(guestDAO.getAllGuests());
    }

    @FXML
    private void searchGuests() {
        String term = searchField.getText();
        if (term != null && !term.isEmpty()) {
            guestsTable.setItems(guestDAO.searchGuests(term));
        } else {
            refreshGuests();
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refreshGuests();
    }

    @FXML
    private void showAddGuestForm() {
        selectedGuest = null;
        clearForm();
        guestFormPanel.setVisible(true);
        guestFormPanel.setManaged(true);
    }

    @FXML
    private void editGuest() {
        selectedGuest = guestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest == null) {
            AlertUtil.showWarning("No Selection", "No Guest Selected", "Please select a guest to edit.");
            return;
        }

        firstNameField.setText(selectedGuest.getFirstName());
        lastNameField.setText(selectedGuest.getLastName());
        emailField.setText(selectedGuest.getEmail());
        phoneField.setText(selectedGuest.getPhone());
        idNumberField.setText(selectedGuest.getIdNumber());
        addressArea.setText(selectedGuest.getAddress());

        guestFormPanel.setVisible(true);
        guestFormPanel.setManaged(true);
    }

    @FXML
    private void saveGuest() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }

        if (selectedGuest == null) {
            // Add new guest
            Guest guest = new Guest(0,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    idNumberField.getText(),
                    addressArea.getText());

            if (guestDAO.addGuest(guest)) {
                AlertUtil.showSuccess("Success", "Guest Added", " Guest has been added successfully.");
                cancelGuestForm();
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("add guest");
            }
        } else {
            // Update existing guest
            selectedGuest.setFirstName(firstNameField.getText());
            selectedGuest.setLastName(lastNameField.getText());
            selectedGuest.setEmail(emailField.getText());
            selectedGuest.setPhone(phoneField.getText());
            selectedGuest.setIdNumber(idNumberField.getText());
            selectedGuest.setAddress(addressArea.getText());

            if (guestDAO.updateGuest(selectedGuest)) {
                AlertUtil.showSuccess("Success", "Guest Updated", "Guest has been updated successfully.");
                cancelGuestForm();
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("update guest");
            }
        }
    }

    @FXML
    private void deleteGuest() {
        Guest guest = guestsTable.getSelectionModel().getSelectedItem();
        if (guest == null) {
            AlertUtil.showWarning("No Selection", "No Guest Selected", "Please select a guest to delete.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Delete Guest",
                "Are you sure you want to delete " + guest.getFullName() + "?")) {
            if (guestDAO.deleteGuest(guest.getId())) {
                AlertUtil.showSuccess("Success", "Guest Deleted", "Guest has been deleted successfully.");
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("delete guest");
            }
        }
    }

    @FXML
    private void cancelGuestForm() {
        guestFormPanel.setVisible(false);
        guestFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        idNumberField.clear();
        addressArea.clear();
    }

    private String validate() {
        String error;
        error = ValidationUtil.getRequiredFieldError("First Name", firstNameField.getText());
        if (error != null)
            return error;

        error = ValidationUtil.getRequiredFieldError("Last Name", lastNameField.getText());
        if (error != null)
            return error;

        error = ValidationUtil.getEmailError(emailField.getText());
        if (error != null)
            return error;

        error = ValidationUtil.getPhoneError(phoneField.getText());
        if (error != null)
            return error;

        error = ValidationUtil.getRequiredFieldError("ID Number", idNumberField.getText());
        if (error != null)
            return error;

        return null;
    }

    // Navigation methods
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