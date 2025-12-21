package com.hotel.controller;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;
import com.hotel.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RoomController {
    @FXML
    private TableView<Room> roomsTable;
    @FXML
    private TableColumn<Room, String> roomNumberColumn;
    @FXML
    private TableColumn<Room, String> typeColumn;
    @FXML
    private TableColumn<Room, Integer> maxOccupancyColumn;
    @FXML
    private TableColumn<Room, Double> priceColumn;
    @FXML
    private TableColumn<Room, String> statusColumn;
    @FXML
    private TableColumn<Room, String> descriptionColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<RoomType> typeFilter;
    @FXML
    private ComboBox<RoomStatus> statusFilter;

    @FXML
    private Label totalRoomsLabel;
    @FXML
    private Label availableRoomsLabel;
    @FXML
    private Label occupiedRoomsLabel;
    @FXML
    private Label maintenanceRoomsLabel;

    @FXML
    private VBox roomFormPanel;
    @FXML
    private TextField roomNumberField;
    @FXML
    private ComboBox<RoomType> typeComboBox;
    @FXML
    private TextField maxOccupancyField;
    @FXML
    private TextField pricePerNightField;
    @FXML
    private ComboBox<RoomStatus> statusComboBox;
    @FXML
    private TextArea descriptionArea;

    private RoomDAO roomDAO = new RoomDAO();
    private Room selectedRoom = null;
    private ObservableList<Room> allRooms;

    @FXML
    public void initialize() {
        // Setup table columns
        roomNumberColumn.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty());
        typeColumn.setCellValueFactory(cellData -> {
            RoomType type = cellData.getValue().getType();
            return new javafx.beans.property.SimpleStringProperty(type.getDisplayName());
        });
        maxOccupancyColumn.setCellValueFactory(cellData -> cellData.getValue().maxOccupancyProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerNightProperty().asObject());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<Room, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        // Format status column
        statusColumn.setCellValueFactory(cellData -> {
            RoomStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status.getDisplayName());
        });
        statusColumn.setCellFactory(column -> new TableCell<Room, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Available")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (status.equals("Occupied")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Populate dropdowns
        typeComboBox.setItems(FXCollections.observableArrayList(RoomType.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(RoomStatus.values()));
        typeFilter.setItems(FXCollections.observableArrayList(RoomType.values()));
        statusFilter.setItems(FXCollections.observableArrayList(RoomStatus.values()));

        try {
            refreshRooms();
            updateStatistics();
        } catch (Exception e) {
            System.err.println("Warning: Could not load rooms from database: " + e.getMessage());
            // View will load anyway, just with empty table
        }
    }

    @FXML
    private void refreshRooms() {
        allRooms = roomDAO.getAllRooms();
        roomsTable.setItems(allRooms);
        updateStatistics();
    }

    private void updateStatistics() {
        if (allRooms == null) {
            allRooms = roomDAO.getAllRooms();
        }

        totalRoomsLabel.setText(String.valueOf(allRooms.size()));

        int available = 0, occupied = 0, maintenance = 0;
        for (Room room : allRooms) {
            switch (room.getStatus()) {
                case AVAILABLE:
                    available++;
                    break;
                case OCCUPIED:
                    occupied++;
                    break;
                case MAINTENANCE:
                    maintenance++;
                    break;
            }
        }

        availableRoomsLabel.setText(String.valueOf(available));
        occupiedRoomsLabel.setText(String.valueOf(occupied));
        maintenanceRoomsLabel.setText(String.valueOf(maintenance));
    }

    @FXML
    private void searchRooms() {
        String term = searchField.getText().toLowerCase().trim();
        if (term.isEmpty()) {
            roomsTable.setItems(allRooms);
            return;
        }

        ObservableList<Room> filteredRooms = FXCollections.observableArrayList();
        for (Room room : allRooms) {
            if (room.getRoomNumber().toLowerCase().contains(term) ||
                    room.getType().getDisplayName().toLowerCase().contains(term) ||
                    room.getDescription().toLowerCase().contains(term)) {
                filteredRooms.add(room);
            }
        }
        roomsTable.setItems(filteredRooms);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        typeFilter.setValue(null);
        statusFilter.setValue(null);
        refreshRooms();
    }

    @FXML
    private void applyFilters() {
        ObservableList<Room> filteredRooms = FXCollections.observableArrayList(allRooms);

        // Filter by type
        RoomType selectedType = typeFilter.getValue();
        if (selectedType != null) {
            filteredRooms.removeIf(room -> room.getType() != selectedType);
        }

        // Filter by status
        RoomStatus selectedStatus = statusFilter.getValue();
        if (selectedStatus != null) {
            filteredRooms.removeIf(room -> room.getStatus() != selectedStatus);
        }

        roomsTable.setItems(filteredRooms);
    }

    @FXML
    private void showAddRoomForm() {
        selectedRoom = null;
        clearForm();
        roomFormPanel.setVisible(true);
        roomFormPanel.setManaged(true);
    }

    @FXML
    private void editRoom() {
        selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            AlertUtil.showWarning("No Selection", "No Room Selected", "Please select a room to edit.");
            return;
        }

        roomNumberField.setText(selectedRoom.getRoomNumber());
        typeComboBox.setValue(selectedRoom.getType());
        maxOccupancyField.setText(String.valueOf(selectedRoom.getMaxOccupancy()));
        pricePerNightField.setText(String.valueOf(selectedRoom.getPricePerNight()));
        statusComboBox.setValue(selectedRoom.getStatus());
        descriptionArea.setText(selectedRoom.getDescription());

        roomFormPanel.setVisible(true);
        roomFormPanel.setManaged(true);
    }

    @FXML
    private void saveRoom() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }

        String roomNumber = roomNumberField.getText().trim();
        int maxOccupancy = Integer.parseInt(maxOccupancyField.getText());
        double pricePerNight = Double.parseDouble(pricePerNightField.getText());

        if (selectedRoom == null) {
            // Check if room number already exists
            if (roomDAO.getRoomByNumber(roomNumber) != null) {
                AlertUtil.showValidationError("Room number '" + roomNumber + "' already exists.");
                return;
            }

            // Add new room
            Room room = new Room(
                    roomNumber,
                    typeComboBox.getValue(),
                    statusComboBox.getValue(),
                    pricePerNight,
                    descriptionArea.getText().trim(),
                    maxOccupancy);

            if (roomDAO.addRoom(room)) {
                AlertUtil.showSuccess("Success", "Room Added", "Room has been added successfully.");
                cancelRoomForm();
                refreshRooms();
            } else {
                AlertUtil.showDatabaseError("add room");
            }
        } else {
            // Update existing room
            selectedRoom.setRoomNumber(roomNumber);
            selectedRoom.setType(typeComboBox.getValue());
            selectedRoom.setMaxOccupancy(maxOccupancy);
            selectedRoom.setPricePerNight(pricePerNight);
            selectedRoom.setStatus(statusComboBox.getValue());
            selectedRoom.setDescription(descriptionArea.getText().trim());

            if (roomDAO.updateRoom(selectedRoom)) {
                AlertUtil.showSuccess("Success", "Room Updated", "Room has been updated successfully.");
                cancelRoomForm();
                refreshRooms();
            } else {
                AlertUtil.showDatabaseError("update room");
            }
        }
    }

    @FXML
    private void deleteRoom() {
        Room room = roomsTable.getSelectionModel().getSelectedItem();
        if (room == null) {
            AlertUtil.showWarning("No Selection", "No Room Selected", "Please select a room to delete.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Delete Room",
                "Are you sure you want to delete Room " + room.getRoomNumber() + "?")) {
            if (roomDAO.deleteRoom(room.getRoomNumber())) {
                AlertUtil.showSuccess("Success", "Room Deleted", "Room has been deleted successfully.");
                refreshRooms();
            } else {
                AlertUtil.showDatabaseError("delete room");
            }
        }
    }

    @FXML
    private void changeRoomStatus() {
        Room room = roomsTable.getSelectionModel().getSelectedItem();
        if (room == null) {
            AlertUtil.showWarning("No Selection", "No Room Selected", "Please select a room to change its status.");
            return;
        }

        // Show a dialog to select new status
        ChoiceDialog<RoomStatus> dialog = new ChoiceDialog<>(room.getStatus(), RoomStatus.values());
        dialog.setTitle("Change Room Status");
        dialog.setHeaderText("Change status for Room " + room.getRoomNumber());
        dialog.setContentText("Select new status:");

        dialog.showAndWait().ifPresent(newStatus -> {
            room.setStatus(newStatus);
            if (roomDAO.updateRoom(room)) {
                AlertUtil.showSuccess("Success", "Status Updated",
                        "Room status has been changed to " + newStatus.getDisplayName() + ".");
                refreshRooms();
            } else {
                AlertUtil.showDatabaseError("update room status");
            }
        });
    }

    @FXML
    private void cancelRoomForm() {
        roomFormPanel.setVisible(false);
        roomFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        roomNumberField.clear();
        typeComboBox.setValue(null);
        maxOccupancyField.clear();
        pricePerNightField.clear();
        statusComboBox.setValue(RoomStatus.AVAILABLE);
        descriptionArea.clear();
    }

    private String validate() {
        String error;

        error = ValidationUtil.getRequiredFieldError("Room Number", roomNumberField.getText());
        if (error != null)
            return error;

        if (typeComboBox.getValue() == null) {
            return "Please select a room type.";
        }

        if (statusComboBox.getValue() == null) {
            return "Please select a room status.";
        }

        error = ValidationUtil.getRequiredFieldError("Max Occupancy", maxOccupancyField.getText());
        if (error != null)
            return error;

        try {
            int occupancy = Integer.parseInt(maxOccupancyField.getText());
            if (occupancy <= 0) {
                return "Max occupancy must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            return "Max occupancy must be a valid number.";
        }

        error = ValidationUtil.getRequiredFieldError("Price Per Night", pricePerNightField.getText());
        if (error != null)
            return error;

        try {
            double price = Double.parseDouble(pricePerNightField.getText());
            if (price <= 0) {
                return "Price per night must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            return "Price per night must be a valid number.";
        }

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
    private void goToServices() {
        NavigationUtil.loadServices();
    }
}
