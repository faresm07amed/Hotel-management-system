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

        refreshReservations();
    }

    @FXML
    private void refreshReservations() {
        allReservations = reservationDAO.getAllReservations();
        reservationsTable.setItems(allReservations);
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
        // Load all guests
        guestComboBox.setItems(guestDAO.getAllGuests());

        // Load available rooms
        roomComboBox.setItems(roomDAO.getRoomsByStatus(RoomStatus.AVAILABLE));
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
            Reservation reservation = new Reservation(0,
                    guest,
                    room,
                    checkIn,
                    checkOut,
                    statusComboBox.getValue(),
                    totalPrice,
                    notesArea.getText().trim());

            if (reservationDAO.addReservation(reservation)) {
                // Update room status if confirmed or checked in
                if (reservation.getStatus() == ReservationStatus.CONFIRMED ||
                        reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                    room.setStatus(RoomStatus.OCCUPIED);
                    roomDAO.updateRoom(room);
                }

                AlertUtil.showSuccess("Success", "Reservation Created", "Reservation has been created successfully.");
                cancelReservationForm();
                refreshReservations();
            } else {
                AlertUtil.showDatabaseError("create reservation");
            }
        } else {
            // Update existing reservation
            selectedReservation.setGuest(guest);
            selectedReservation.setRoom(room);
            selectedReservation.setCheckInDate(checkIn);
            selectedReservation.setCheckOutDate(checkOut);
            selectedReservation.setStatus(statusComboBox.getValue());
            selectedReservation.setTotalPrice(totalPrice);
            selectedReservation.setNotes(notesArea.getText().trim());

            if (reservationDAO.updateReservation(selectedReservation)) {
                AlertUtil.showSuccess("Success", "Reservation Updated", "Reservation has been updated successfully.");
                cancelReservationForm();
                refreshReservations();
            } else {
                AlertUtil.showDatabaseError("update reservation");
            }
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
        if (reservationDAO.updateReservation(reservation)) {
            // Update room status
            Room room = reservation.getRoom();
            room.setStatus(RoomStatus.OCCUPIED);
            roomDAO.updateRoom(room);

            AlertUtil.showSuccess("Success", "Checked In", "Guest has been checked in successfully.");
            refreshReservations();
        } else {
            AlertUtil.showDatabaseError("check in reservation");
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
        if (reservationDAO.updateReservation(reservation)) {
            // Update room status
            Room room = reservation.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.updateRoom(room);

            AlertUtil.showSuccess("Success", "Checked Out", "Guest has been checked out successfully.");
            refreshReservations();
        } else {
            AlertUtil.showDatabaseError("check out reservation");
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
            if (reservationDAO.updateReservation(reservation)) {
                // Update room status if it was occupied
                if (reservation.getRoom().getStatus() == RoomStatus.OCCUPIED) {
                    Room room = reservation.getRoom();
                    room.setStatus(RoomStatus.AVAILABLE);
                    roomDAO.updateRoom(room);
                }

                AlertUtil.showSuccess("Success", "Reservation Cancelled", "Reservation has been cancelled.");
                refreshReservations();
            } else {
                AlertUtil.showDatabaseError("cancel reservation");
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
