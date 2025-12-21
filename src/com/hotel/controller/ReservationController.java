package com.hotel.controller;

import com.hotel.dao.*;
import com.hotel.model.*;
import com.hotel.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationController {
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, Integer> idColumn;
    @FXML
    private TableColumn<Reservation, String> guestNameColumn;
    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;
    @FXML
    private TableColumn<Reservation, LocalDate> checkInColumn;
    @FXML
    private TableColumn<Reservation, LocalDate> checkOutColumn;
    @FXML
    private TableColumn<Reservation, Long> nightsColumn;
    @FXML
    private TableColumn<Reservation, Double> totalPriceColumn;
    @FXML
    private TableColumn<Reservation, String> statusColumn;
    @FXML
    private TableColumn<Reservation, String> notesColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<ReservationStatus> statusFilter;

    @FXML
    private VBox reservationFormPanel;
    @FXML
    private ComboBox<Guest> guestComboBox;
    @FXML
    private ComboBox<Room> roomComboBox;
    @FXML
    private DatePicker checkInDatePicker;
    @FXML
    private DatePicker checkOutDatePicker;
    @FXML
    private Label numberOfNightsLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label roomPriceLabel;
    @FXML
    private ComboBox<ReservationStatus> statusComboBox;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label conflictWarningLabel;

    private ReservationDAO reservationDAO = new ReservationDAO();
    private GuestDAO guestDAO = new GuestDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private Reservation selectedReservation = null;
    private ObservableList<Reservation> allReservations;

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        guestNameColumn.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getGuestName();
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        roomNumberColumn.setCellValueFactory(cellData -> {
            String roomNum = cellData.getValue().getRoomNumber();
            return new javafx.beans.property.SimpleStringProperty(roomNum);
        });
        checkInColumn.setCellValueFactory(cellData -> cellData.getValue().checkInDateProperty());
        checkOutColumn.setCellValueFactory(cellData -> cellData.getValue().checkOutDateProperty());

        // Calculate nights
        nightsColumn.setCellValueFactory(cellData -> {
            LocalDate checkIn = cellData.getValue().getCheckInDate();
            LocalDate checkOut = cellData.getValue().getCheckOutDate();
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            return new javafx.beans.property.SimpleLongProperty(nights).asObject();
        });

        totalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        // Format price column
        totalPriceColumn.setCellFactory(column -> new TableCell<Reservation, Double>() {
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
            ReservationStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status.getDisplayName());
        });
        statusColumn.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Pending":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "Confirmed":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "Checked In":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "Checked Out":
                            setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                            break;
                        case "Cancelled":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        notesColumn.setCellValueFactory(cellData -> cellData.getValue().notesProperty());

        // Populate dropdowns
        statusComboBox.setItems(FXCollections.observableArrayList(ReservationStatus.values()));
        statusFilter.setItems(FXCollections.observableArrayList(ReservationStatus.values()));

        try {
            refreshReservations();
        } catch (Exception e) {
            System.err.println("Warning: Could not load reservations from database: " + e.getMessage());
            // View will load anyway, just with empty table
        }
    }

    @FXML
    private void refreshReservations() {
        try {
            allReservations = reservationDAO.getAllReservations();
            System.out.println("DEBUG: getAllReservations returned: " +
                    (allReservations == null ? "null" : allReservations.size() + " items"));

            if (allReservations != null && !allReservations.isEmpty()) {
                System.out.println("DEBUG: Using database reservations");
                reservationsTable.setItems(allReservations);
            } else {
                // Add sample data if database is empty or unavailable
                System.out.println("DEBUG: Database empty, loading sample data");
                allReservations = createSampleReservations();
                System.out.println("DEBUG: Sample data created: " + allReservations.size() + " items");
                reservationsTable.setItems(allReservations);
            }
        } catch (Exception e) {
            System.err.println("Error refreshing reservations: " + e.getMessage());
            e.printStackTrace();
            // Load sample data on error
            System.out.println("DEBUG: Error occurred, loading sample data");
            allReservations = createSampleReservations();
            reservationsTable.setItems(allReservations);
        }
    }

    private ObservableList<Reservation> createSampleReservations() {
        ObservableList<Reservation> sampleData = FXCollections.observableArrayList();

        // Create sample guests
        Guest guest1 = new Guest(1, "John", "Doe", "john.doe@email.com", "555-0101", "ID001", "123 Main St");
        Guest guest2 = new Guest(2, "Jane", "Smith", "jane.smith@email.com", "555-0102", "ID002", "456 Oak Ave");
        Guest guest3 = new Guest(3, "Bob", "Johnson", "bob.j@email.com", "555-0103", "ID003", "789 Pine Rd");

        // Create sample rooms
        Room room1 = new Room("101", RoomType.SINGLE, RoomStatus.OCCUPIED, 100.0, "Standard single room", 1);
        Room room2 = new Room("201", RoomType.DOUBLE, RoomStatus.AVAILABLE, 150.0, "Deluxe double room", 2);
        Room room3 = new Room("301", RoomType.SUITE, RoomStatus.OCCUPIED, 250.0, "Luxury suite", 4);

        // Create sample reservations
        sampleData.add(new Reservation(1, guest1, room1,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(3),
                ReservationStatus.CHECKED_IN, 500.0, "Early check-in requested"));

        sampleData.add(new Reservation(2, guest2, room2,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(8),
                ReservationStatus.CONFIRMED, 450.0, "Honeymoon package"));

        sampleData.add(new Reservation(3, guest3, room3,
                LocalDate.now().minusDays(7), LocalDate.now().minusDays(2),
                ReservationStatus.CHECKED_OUT, 1250.0, "Business trip"));

        sampleData.add(new Reservation(4, guest1, room2,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(14),
                ReservationStatus.PENDING, 600.0, "Waiting for confirmation"));

        sampleData.add(new Reservation(5, guest2, room3,
                LocalDate.now().minusDays(15), LocalDate.now().minusDays(10),
                ReservationStatus.CANCELLED, 1250.0, "Client cancelled"));

        return sampleData;
    }

    @FXML
    private void searchReservations() {
        String term = searchField.getText().toLowerCase().trim();
        if (term.isEmpty()) {
            reservationsTable.setItems(allReservations);
            return;
        }

        ObservableList<Reservation> filteredReservations = FXCollections.observableArrayList();
        for (Reservation reservation : allReservations) {
            if (reservation.getGuestName().toLowerCase().contains(term) ||
                    reservation.getRoomNumber().toLowerCase().contains(term) ||
                    String.valueOf(reservation.getId()).contains(term)) {
                filteredReservations.add(reservation);
            }
        }
        reservationsTable.setItems(filteredReservations);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        statusFilter.setValue(null);
        refreshReservations();
    }

    @FXML
    private void applyFilters() {
        ReservationStatus selectedStatus = statusFilter.getValue();
        if (selectedStatus == null) {
            reservationsTable.setItems(allReservations);
            return;
        }

        ObservableList<Reservation> filteredReservations = reservationDAO.getReservationsByStatus(selectedStatus);
        reservationsTable.setItems(filteredReservations);
    }

    @FXML
    private void showAddReservationForm() {
        selectedReservation = null;
        clearForm();
        loadGuestsAndRooms();
        reservationFormPanel.setVisible(true);
        reservationFormPanel.setManaged(true);
    }

    private void loadGuestsAndRooms() {
        try {
            // Load all guests
            ObservableList<Guest> guests = guestDAO.getAllGuests();
            if (guests != null) {
                guestComboBox.setItems(guests);
            } else {
                guestComboBox.setItems(FXCollections.observableArrayList());
            }

            // Load available rooms
            ObservableList<Room> rooms = roomDAO.getRoomsByStatus(RoomStatus.AVAILABLE);
            if (rooms != null) {
                roomComboBox.setItems(rooms);
            } else {
                roomComboBox.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            System.err.println("Error loading guests and rooms: " + e.getMessage());
            guestComboBox.setItems(FXCollections.observableArrayList());
            roomComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void editReservation() {
        selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            AlertUtil.showWarning("No Selection", "No Reservation Selected", "Please select a reservation to edit.");
            return;
        }

        loadGuestsAndRooms();

        guestComboBox.setValue(selectedReservation.getGuest());
        roomComboBox.setValue(selectedReservation.getRoom());
        checkInDatePicker.setValue(selectedReservation.getCheckInDate());
        checkOutDatePicker.setValue(selectedReservation.getCheckOutDate());
        statusComboBox.setValue(selectedReservation.getStatus());
        notesArea.setText(selectedReservation.getNotes());

        calculateTotal();

        reservationFormPanel.setVisible(true);
        reservationFormPanel.setManaged(true);
    }

    @FXML
    private void calculateTotal() {
        Room selectedRoom = roomComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom != null) {
            roomPriceLabel.setText(String.format("$%.2f/night", selectedRoom.getPricePerNight()));
        }

        if (checkIn != null && checkOut != null && selectedRoom != null) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights > 0) {
                double total = nights * selectedRoom.getPricePerNight();
                numberOfNightsLabel.setText(nights + " night" + (nights > 1 ? "s" : ""));
                totalPriceLabel.setText(String.format("$%.2f", total));
                checkForConflicts();
            } else {
                numberOfNightsLabel.setText("0 nights");
                totalPriceLabel.setText("$0.00");
                conflictWarningLabel.setText("Check-out date must be after check-in date");
            }
        } else {
            numberOfNightsLabel.setText("0 nights");
            totalPriceLabel.setText("$0.00");
            conflictWarningLabel.setText("");
        }
    }

    private void checkForConflicts() {
        Room selectedRoom = roomComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom == null || checkIn == null || checkOut == null) {
            return;
        }

        // Check if room is already booked for these dates
        ObservableList<Reservation> existingReservations = reservationDAO
                .getReservationsByRoom(selectedRoom.getRoomNumber());

        for (Reservation res : existingReservations) {
            // Skip the current reservation being edited
            if (selectedReservation != null && res.getId() == selectedReservation.getId()) {
                continue;
            }

            // Skip cancelled reservations
            if (res.getStatus() == ReservationStatus.CANCELLED ||
                    res.getStatus() == ReservationStatus.CHECKED_OUT) {
                continue;
            }

            // Check for date overlap
            boolean overlap = !(checkOut.isBefore(res.getCheckInDate()) || checkOut.isEqual(res.getCheckInDate()) ||
                    checkIn.isAfter(res.getCheckOutDate()) || checkIn.isEqual(res.getCheckOutDate()));

            if (overlap) {
                conflictWarningLabel.setText("⚠ Room is already booked for selected dates!");
                return;
            }
        }

        conflictWarningLabel.setText("");
    }

    @FXML
    private void saveReservation() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }

        Guest guest = guestComboBox.getValue();
        Room room = roomComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * room.getPricePerNight();

        if (selectedReservation == null) {
            // Add new reservation
            // Generate a unique ID for the reservation (max ID + 1)
            int newId = allReservations.stream()
                    .mapToInt(Reservation::getId)
                    .max()
                    .orElse(0) + 1;

            Reservation reservation = new Reservation(newId,
                    guest,
                    room,
                    checkIn,
                    checkOut,
                    statusComboBox.getValue(),
                    totalPrice,
                    notesArea.getText().trim());

            boolean dbSuccess = false;
            try {
                dbSuccess = reservationDAO.addReservation(reservation);
            } catch (Exception e) {
                System.err.println("Database error: " + e.getMessage());
            }

            if (dbSuccess) {
                // Update room status if confirmed or checked in
                if (reservation.getStatus() == ReservationStatus.CONFIRMED ||
                        reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                    room.setStatus(RoomStatus.OCCUPIED);
                    try {
                        roomDAO.updateRoom(room);
                    } catch (Exception e) {
                        System.err.println("Error updating room status: " + e.getMessage());
                    }
                }

                // Add to local list and update table
                allReservations.add(reservation);
                reservationsTable.setItems(allReservations);
                // Also sort by ID or newest first if needed, but for now just add

                AlertUtil.showSuccess("Success", "Reservation Created", "Reservation has been created successfully.");
            } else {
                // Database failed, but add to local list for testing
                System.out.println("DEBUG: Database unavailable, adding to local list");
                allReservations.add(reservation);
                reservationsTable.setItems(allReservations);

                AlertUtil.showSuccess("Success", "Reservation Created (Demo Mode)",
                        "Reservation added to table. Note: Changes won't persist without database connection.");
            }

            cancelReservationForm();
        } else {
            // Update existing reservation
            selectedReservation.setGuest(guest);
            selectedReservation.setRoom(room);
            selectedReservation.setCheckInDate(checkIn);
            selectedReservation.setCheckOutDate(checkOut);
            selectedReservation.setStatus(statusComboBox.getValue());
            selectedReservation.setTotalPrice(totalPrice);
            selectedReservation.setNotes(notesArea.getText().trim());

            boolean dbSuccess = false;
            try {
                dbSuccess = reservationDAO.updateReservation(selectedReservation);
            } catch (Exception e) {
                System.err.println("Database error during update: " + e.getMessage());
            }

            if (dbSuccess) {
                AlertUtil.showSuccess("Success", "Reservation Updated", "Reservation has been updated successfully.");
            } else {
                AlertUtil.showSuccess("Success", "Reservation Updated (Demo Mode)",
                        "Changes applied to table. Note: Changes won't persist without database connection.");
            }

            reservationsTable.refresh();
            cancelReservationForm();
        }
    }

    @FXML
    private void checkInReservation() {
        Reservation reservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (reservation == null) {
            AlertUtil.showWarning("No Selection", "No Reservation Selected",
                    "Please select a reservation to check in.");
            return;
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            AlertUtil.showWarning("Invalid Status", "Cannot Check In",
                    "Only confirmed reservations can be checked in.");
            return;
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        boolean dbSuccess = false;
        try {
            dbSuccess = reservationDAO.updateReservation(reservation);
        } catch (Exception e) {
            System.err.println("Database error during check-in: " + e.getMessage());
        }

        if (dbSuccess) {
            // Update room status
            Room room = reservation.getRoom();
            room.setStatus(RoomStatus.OCCUPIED);
            try {
                roomDAO.updateRoom(room);
            } catch (Exception e) {
                System.err.println("Error updating room status: " + e.getMessage());
            }

            AlertUtil.showSuccess("Success", "Checked In", "Guest has been checked in successfully.");
            refreshReservations();
        } else {
            // Demo mode fallback
            reservationsTable.refresh();
            AlertUtil.showSuccess("Success", "Checked In (Demo Mode)",
                    "Guest status updated in table. Note: Changes won't persist without database.");
        }
    }

    @FXML
    private void checkOutReservation() {
        Reservation reservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (reservation == null) {
            AlertUtil.showWarning("No Selection", "No Reservation Selected",
                    "Please select a reservation to check out.");
            return;
        }

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            AlertUtil.showWarning("Invalid Status", "Cannot Check Out",
                    "Only checked-in reservations can be checked out.");
            return;
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        boolean dbSuccess = false;
        try {
            dbSuccess = reservationDAO.updateReservation(reservation);
        } catch (Exception e) {
            System.err.println("Database error during check-out: " + e.getMessage());
        }

        if (dbSuccess) {
            // Update room status
            Room room = reservation.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            try {
                roomDAO.updateRoom(room);
            } catch (Exception e) {
                System.err.println("Error updating room status: " + e.getMessage());
            }

            AlertUtil.showSuccess("Success", "Checked Out", "Guest has been checked out successfully.");
            refreshReservations();
        } else {
            // Demo mode fallback
            reservationsTable.refresh();
            AlertUtil.showSuccess("Success", "Checked Out (Demo Mode)",
                    "Guest status updated in table. Note: Changes won't persist without database.");
        }
    }

    @FXML
    private void cancelReservation() {
        Reservation reservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (reservation == null) {
            AlertUtil.showWarning("No Selection", "No Reservation Selected", "Please select a reservation to cancel.");
            return;
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT ||
                reservation.getStatus() == ReservationStatus.CANCELLED) {
            AlertUtil.showWarning("Invalid Status", "Cannot Cancel",
                    "This reservation is already completed or cancelled.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Cancellation", "Cancel Reservation",
                "Are you sure you want to cancel this reservation?")) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            boolean dbSuccess = false;
            try {
                dbSuccess = reservationDAO.updateReservation(reservation);
            } catch (Exception e) {
                System.err.println("Database error during cancellation: " + e.getMessage());
            }

            if (dbSuccess) {
                // Update room status if it was occupied
                if (reservation.getRoom().getStatus() == RoomStatus.OCCUPIED) {
                    Room room = reservation.getRoom();
                    room.setStatus(RoomStatus.AVAILABLE);
                    try {
                        roomDAO.updateRoom(room);
                    } catch (Exception e) {
                        System.err.println("Error updating room status: " + e.getMessage());
                    }
                }

                AlertUtil.showSuccess("Success", "Reservation Cancelled", "Reservation has been cancelled.");
                refreshReservations();
            } else {
                // Demo mode fallback
                reservationsTable.refresh();
                AlertUtil.showSuccess("Success", "Reservation Cancelled (Demo Mode)",
                        "Reservation status updated in table. Note: Changes won't persist without database.");
            }
        }
    }

    @FXML
    private void viewReservationDetails() {
        Reservation reservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (reservation == null) {
            AlertUtil.showWarning("No Selection", "No Reservation Selected", "Please select a reservation to view.");
            return;
        }

        String details = String.format(
                "Reservation #%d\n\n" +
                        "Guest: %s\n" +
                        "Room: %s\n" +
                        "Check-In: %s\n" +
                        "Check-Out: %s\n" +
                        "Nights: %d\n" +
                        "Total Price: $%.2f\n" +
                        "Status: %s\n" +
                        "Notes: %s",
                reservation.getId(),
                reservation.getGuestName(),
                reservation.getRoomNumber(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate()),
                reservation.getTotalPrice(),
                reservation.getStatus().getDisplayName(),
                reservation.getNotes().isEmpty() ? "None" : reservation.getNotes());

        AlertUtil.showInfo("Reservation Details", "Reservation Information", details);
    }

    @FXML
    private void cancelReservationForm() {
        reservationFormPanel.setVisible(false);
        reservationFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        guestComboBox.setValue(null);
        roomComboBox.setValue(null);
        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));
        statusComboBox.setValue(ReservationStatus.PENDING);
        notesArea.clear();
        numberOfNightsLabel.setText("0 nights");
        totalPriceLabel.setText("$0.00");
        roomPriceLabel.setText("$0.00/night");
        conflictWarningLabel.setText("");
    }

    private String validate() {
        if (guestComboBox.getValue() == null) {
            return "Please select a guest.";
        }

        if (roomComboBox.getValue() == null) {
            return "Please select a room.";
        }

        if (checkInDatePicker.getValue() == null) {
            return "Please select a check-in date.";
        }

        if (checkOutDatePicker.getValue() == null) {
            return "Please select a check-out date.";
        }

        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn.isBefore(LocalDate.now())) {
            return "Check-in date cannot be in the past.";
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            return "Check-out date must be after check-in date.";
        }

        if (statusComboBox.getValue() == null) {
            return "Please select a status.";
        }

        // Check for conflicts
        if (!conflictWarningLabel.getText().isEmpty()) {
            return "The selected room is already booked for these dates.";
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
