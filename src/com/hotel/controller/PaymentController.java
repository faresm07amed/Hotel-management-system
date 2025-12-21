package com.hotel.controller;

import com.hotel.dao.*;
import com.hotel.model.*;
import com.hotel.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentController {
    @FXML
    private TableView<Payment> paymentsTable;
    @FXML
    private TableColumn<Payment, Integer> idColumn;
    @FXML
    private TableColumn<Payment, Integer> reservationIdColumn;
    @FXML
    private TableColumn<Payment, String> guestNameColumn;
    @FXML
    private TableColumn<Payment, Double> amountColumn;
    @FXML
    private TableColumn<Payment, String> methodColumn;
    @FXML
    private TableColumn<Payment, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Payment, String> statusColumn;
    @FXML
    private TableColumn<Payment, String> transactionIdColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<PaymentMethod> methodFilter;
    @FXML
    private ComboBox<PaymentStatus> statusFilter;

    @FXML
    private Label totalPaymentsLabel;
    @FXML
    private Label completedPaymentsLabel;
    @FXML
    private Label totalRevenueLabel;

    @FXML
    private VBox paymentFormPanel;
    @FXML
    private ComboBox<Reservation> reservationComboBox;
    @FXML
    private Label reservationTotalLabel;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<PaymentMethod> methodComboBox;
    @FXML
    private TextField transactionIdField;
    @FXML
    private ComboBox<PaymentStatus> paymentStatusComboBox;
    @FXML
    private TextArea notesArea;

    private PaymentDAO paymentDAO = new PaymentDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private ObservableList<Payment> allPayments;

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        reservationIdColumn.setCellValueFactory(cellData -> {
            int resId = cellData.getValue().getReservationId();
            return new javafx.beans.property.SimpleIntegerProperty(resId).asObject();
        });
        guestNameColumn.setCellValueFactory(cellData -> {
            Reservation res = cellData.getValue().getReservation();
            String guestName = (res != null && res.getGuest() != null) ? res.getGuest().getFullName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(guestName);
        });
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        // Format amount column
        amountColumn.setCellFactory(column -> new TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        methodColumn.setCellValueFactory(cellData -> {
            PaymentMethod method = cellData.getValue().getPaymentMethod();
            return new javafx.beans.property.SimpleStringProperty(method.getDisplayName());
        });

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().paymentDateProperty());
        dateColumn.setCellFactory(column -> new TableCell<Payment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    setText(dateTime.format(formatter));
                }
            }
        });

        // Format status column
        statusColumn.setCellValueFactory(cellData -> {
            PaymentStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status.getDisplayName());
        });
        statusColumn.setCellFactory(column -> new TableCell<Payment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Completed")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (status.equals("Pending")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        transactionIdColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty());

        // Populate dropdowns
        methodComboBox.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        paymentStatusComboBox.setItems(FXCollections.observableArrayList(PaymentStatus.values()));
        methodFilter.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        statusFilter.setItems(FXCollections.observableArrayList(PaymentStatus.values()));

        try {
            refreshPayments();
            updateStatistics();
        } catch (Exception e) {
            System.err.println("Warning: Could not load payments from database: " + e.getMessage());
            // View will load anyway, just with empty table
        }
    }

    @FXML
    private void refreshPayments() {
        try {
            allPayments = paymentDAO.getAllPayments();
            if (allPayments != null && !allPayments.isEmpty()) {
                paymentsTable.setItems(allPayments);
            } else {
                // Load sample data if database is empty or unavailable
                allPayments = createSamplePayments();
                paymentsTable.setItems(allPayments);
            }
            updateStatistics();
        } catch (Exception e) {
            System.err.println("Error refreshing payments: " + e.getMessage());
            // Load sample data on error
            allPayments = createSamplePayments();
            paymentsTable.setItems(allPayments);
            updateStatistics();
        }
    }

    private void updateStatistics() {
        try {
            if (allPayments == null) {
                allPayments = paymentDAO.getAllPayments();
                if (allPayments == null) {
                    allPayments = FXCollections.observableArrayList();
                }
            }

            totalPaymentsLabel.setText(String.valueOf(allPayments.size()));

            int completed = 0;
            double totalRevenue = 0.0;

            for (Payment payment : allPayments) {
                if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
                    completed++;
                    totalRevenue += payment.getAmount();
                }
            }

            completedPaymentsLabel.setText(String.valueOf(completed));
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        } catch (Exception e) {
            System.err.println("Error updating statistics: " + e.getMessage());
            // Set default values
            totalPaymentsLabel.setText("0");
            completedPaymentsLabel.setText("0");
            totalRevenueLabel.setText("$0.00");
        }
    }

    @FXML
    private void searchPayments() {
        String term = searchField.getText().toLowerCase().trim();
        if (term.isEmpty()) {
            paymentsTable.setItems(allPayments);
            return;
        }

        ObservableList<Payment> filteredPayments = FXCollections.observableArrayList();
        for (Payment payment : allPayments) {
            Reservation res = payment.getReservation();
            String guestName = (res != null && res.getGuest() != null) ? res.getGuest().getFullName() : "";

            if (guestName.toLowerCase().contains(term) ||
                    payment.getTransactionId().toLowerCase().contains(term) ||
                    String.valueOf(payment.getId()).contains(term)) {
                filteredPayments.add(payment);
            }
        }
        paymentsTable.setItems(filteredPayments);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        methodFilter.setValue(null);
        statusFilter.setValue(null);
        refreshPayments();
    }

    @FXML
    private void applyFilters() {
        ObservableList<Payment> filteredPayments = FXCollections.observableArrayList(allPayments);

        // Filter by method
        PaymentMethod selectedMethod = methodFilter.getValue();
        if (selectedMethod != null) {
            filteredPayments.removeIf(payment -> payment.getPaymentMethod() != selectedMethod);
        }

        // Filter by status
        PaymentStatus selectedStatus = statusFilter.getValue();
        if (selectedStatus != null) {
            filteredPayments.removeIf(payment -> payment.getStatus() != selectedStatus);
        }

        paymentsTable.setItems(filteredPayments);
    }

    @FXML
    private void showAddPaymentForm() {
        clearForm();
        loadUnpaidReservations();
        paymentFormPanel.setVisible(true);
        paymentFormPanel.setManaged(true);
    }

    private void loadUnpaidReservations() {
        try {
            // Load reservations that are checked in or checked out
            ObservableList<Reservation> reservations = reservationDAO.getAllReservations();

            // If database is empty or unavailable, use sample reservations
            if (reservations == null || reservations.isEmpty()) {
                reservations = createSampleReservationsForPayments();
            }

            ObservableList<Reservation> unpaidReservations = FXCollections.observableArrayList();

            for (Reservation res : reservations) {
                if (res.getStatus() == ReservationStatus.CHECKED_IN ||
                        res.getStatus() == ReservationStatus.CHECKED_OUT) {

                    boolean isPaid = false;
                    try {
                        // Check if already paid
                        ObservableList<Payment> payments = paymentDAO.getPaymentsByReservation(res.getId());
                        for (Payment p : payments) {
                            if (p.getStatus() == PaymentStatus.COMPLETED) {
                                isPaid = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // In demo mode, assume it's not paid yet
                        System.out.println("DEBUG: Database error checking payment status, assuming unpaid");
                    }

                    if (!isPaid) {
                        unpaidReservations.add(res);
                    }
                }
            }

            reservationComboBox.setItems(unpaidReservations);
        } catch (Exception e) {
            System.err.println("Error loading unpaid reservations: " + e.getMessage());
            reservationComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void onReservationSelected() {
        Reservation selected = reservationComboBox.getValue();
        if (selected != null) {
            double total = selected.getTotalPrice();
            reservationTotalLabel.setText(String.format("Total: $%.2f", total));
            amountField.setText(String.valueOf(total));
        } else {
            reservationTotalLabel.setText("Total: $0.00");
            amountField.clear();
        }
    }

    @FXML
    private void savePayment() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }

        Reservation reservation = reservationComboBox.getValue();
        double amount = Double.parseDouble(amountField.getText());
        PaymentMethod method = methodComboBox.getValue();
        PaymentStatus status = paymentStatusComboBox.getValue();
        String transactionId = transactionIdField.getText().trim();
        String notes = notesArea.getText().trim();

        // Generate a temporary ID for demo mode
        int newId = allPayments.stream()
                .mapToInt(Payment::getId)
                .max()
                .orElse(0) + 1;

        Payment payment = new Payment(newId,
                reservation,
                amount,
                method,
                LocalDateTime.now(),
                status,
                transactionId,
                notes);

        boolean dbSuccess = false;
        try {
            dbSuccess = paymentDAO.addPayment(payment);
        } catch (Exception e) {
            System.err.println("Database error during payment recording: " + e.getMessage());
        }

        if (dbSuccess) {
            AlertUtil.showSuccess("Success", "Payment Recorded", "Payment has been recorded successfully.");
            refreshPayments();
        } else {
            // Demo mode fallback
            allPayments.add(0, payment); // Add to top of list
            paymentsTable.setItems(allPayments);
            updateStatistics();

            AlertUtil.showSuccess("Success", "Payment Recorded (Demo Mode)",
                    "Payment added to table. Note: Changes won't persist without database connection.");
        }
        cancelPaymentForm();
    }

    @FXML
    private void viewReceipt() {
        Payment payment = paymentsTable.getSelectionModel().getSelectedItem();
        if (payment == null) {
            AlertUtil.showWarning("No Selection", "No Payment Selected", "Please select a payment to view receipt.");
            return;
        }

        String receipt = ReceiptGenerator.generateReceipt(payment);
        AlertUtil.showInfo("Payment Receipt", "Receipt #" + payment.getId(), receipt);
    }

    @FXML
    private void printReceipt() {
        Payment payment = paymentsTable.getSelectionModel().getSelectedItem();
        if (payment == null) {
            AlertUtil.showWarning("No Selection", "No Payment Selected", "Please select a payment to print receipt.");
            return;
        }

        String receipt = ReceiptGenerator.generateReceipt(payment);
        // In a real application, this would send to a printer
        // For now, we'll show it in a dialog
        AlertUtil.showInfo("Print Receipt", "Receipt #" + payment.getId(),
                receipt + "\n\n(This would be sent to printer)");
    }

    @FXML
    private void refundPayment() {
        Payment payment = paymentsTable.getSelectionModel().getSelectedItem();
        if (payment == null) {
            AlertUtil.showWarning("No Selection", "No Payment Selected", "Please select a payment to refund.");
            return;
        }

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            AlertUtil.showWarning("Invalid Status", "Cannot Refund",
                    "Only completed payments can be refunded.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Refund", "Refund Payment",
                String.format("Are you sure you want to refund $%.2f?", payment.getAmount()))) {
            payment.setStatus(PaymentStatus.REFUNDED);

            boolean dbSuccess = false;
            try {
                dbSuccess = paymentDAO.updatePayment(payment);
            } catch (Exception e) {
                System.err.println("Database error during refund: " + e.getMessage());
            }

            if (dbSuccess) {
                AlertUtil.showSuccess("Success", "Payment Refunded", "Payment has been refunded successfully.");
                refreshPayments();
            } else {
                // Demo mode fallback
                paymentsTable.refresh();
                updateStatistics();
                AlertUtil.showSuccess("Success", "Payment Refunded (Demo Mode)",
                        "Status updated in table. Note: Changes won't persist without database connection.");
            }
        }
    }

    @FXML
    private void cancelPaymentForm() {
        paymentFormPanel.setVisible(false);
        paymentFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        reservationComboBox.setValue(null);
        reservationTotalLabel.setText("Total: $0.00");
        amountField.clear();
        methodComboBox.setValue(null);
        transactionIdField.clear();
        paymentStatusComboBox.setValue(PaymentStatus.COMPLETED);
        notesArea.clear();
    }

    private String validate() {
        if (reservationComboBox.getValue() == null) {
            return "Please select a reservation.";
        }

        String error = ValidationUtil.getRequiredFieldError("Amount", amountField.getText());
        if (error != null)
            return error;

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                return "Amount must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            return "Amount must be a valid number.";
        }

        if (methodComboBox.getValue() == null) {
            return "Please select a payment method.";
        }

        if (paymentStatusComboBox.getValue() == null) {
            return "Please select a payment status.";
        }

        return null;
    }

    private ObservableList<Payment> createSamplePayments() {
        ObservableList<Payment> samplePayments = FXCollections.observableArrayList();
        ObservableList<Reservation> reservations = createSampleReservationsForPayments();

        samplePayments.add(new Payment(1, reservations.get(0), 500.0, PaymentMethod.CREDIT_CARD,
                LocalDateTime.now().minusDays(2), PaymentStatus.COMPLETED, "TXN_123456", "Paid in full at check-in"));

        samplePayments.add(new Payment(2, reservations.get(1), 1250.0, PaymentMethod.CASH,
                LocalDateTime.now().minusDays(5), PaymentStatus.COMPLETED, "TXN_789012", "Business expense"));

        samplePayments.add(new Payment(3, reservations.get(2), 300.0, PaymentMethod.ONLINE,
                LocalDateTime.now().minusHours(12), PaymentStatus.PENDING, "TXN_345678", "Awaiting verification"));

        return samplePayments;
    }

    private ObservableList<Reservation> createSampleReservationsForPayments() {
        ObservableList<Reservation> sampleReservations = FXCollections.observableArrayList();

        Guest guest1 = new Guest(1, "John", "Doe", "john.doe@email.com", "555-0101", "ID001", "123 Main St");
        Guest guest2 = new Guest(2, "Jane", "Smith", "jane.smith@email.com", "555-0102", "ID002", "456 Oak Ave");
        Guest guest3 = new Guest(3, "Bob", "Johnson", "bob.j@email.com", "555-0103", "ID003", "789 Pine Rd");

        Room room1 = new Room("101", RoomType.SINGLE, RoomStatus.OCCUPIED, 100.0, "Standard single room", 1);
        Room room2 = new Room("201", RoomType.DOUBLE, RoomStatus.AVAILABLE, 150.0, "Deluxe double room", 2);
        Room room3 = new Room("301", RoomType.SUITE, RoomStatus.OCCUPIED, 250.0, "Luxury suite", 4);

        sampleReservations.add(new Reservation(1, guest1, room1,
                java.time.LocalDate.now().minusDays(3), java.time.LocalDate.now().plusDays(2),
                ReservationStatus.CHECKED_IN, 500.0, "Sample 1"));

        sampleReservations.add(new Reservation(2, guest3, room3,
                java.time.LocalDate.now().minusDays(10), java.time.LocalDate.now().minusDays(5),
                ReservationStatus.CHECKED_OUT, 1250.0, "Sample 2"));

        sampleReservations.add(new Reservation(3, guest2, room2,
                java.time.LocalDate.now().minusDays(1), java.time.LocalDate.now().plusDays(1),
                ReservationStatus.CHECKED_IN, 300.0, "Sample 3"));

        return sampleReservations;
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
