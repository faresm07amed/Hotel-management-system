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

        refreshPayments();
        updateStatistics();
    }

    @FXML
    private void refreshPayments() {
        allPayments = paymentDAO.getAllPayments();
        paymentsTable.setItems(allPayments);
        updateStatistics();
    }

    private void updateStatistics() {
        if (allPayments == null) {
            allPayments = paymentDAO.getAllPayments();
        }

        totalPaymentsLabel.setText(String.valueOf(allPayments.size()));

        int completed = 0;
        double totalRevenue = 0.0;

        for (Payment payment : allPayments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                completed++;
                totalRevenue += payment.getAmount();
            }
        }

        completedPaymentsLabel.setText(String.valueOf(completed));
        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
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
        // Load reservations that are checked in or checked out
        ObservableList<Reservation> allReservations = reservationDAO.getAllReservations();
        ObservableList<Reservation> unpaidReservations = FXCollections.observableArrayList();

        for (Reservation res : allReservations) {
            if (res.getStatus() == ReservationStatus.CHECKED_IN ||
                    res.getStatus() == ReservationStatus.CHECKED_OUT) {
                // Check if already paid
                ObservableList<Payment> payments = paymentDAO.getPaymentsByReservation(res.getId());
                boolean isPaid = false;
                for (Payment p : payments) {
                    if (p.getStatus() == PaymentStatus.COMPLETED) {
                        isPaid = true;
                        break;
                    }
                }
                if (!isPaid) {
                    unpaidReservations.add(res);
                }
            }
        }

        reservationComboBox.setItems(unpaidReservations);
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

        Payment payment = new Payment(0,
                reservation,
                amount,
                method,
                LocalDateTime.now(),
                status,
                transactionId,
                notes);

        if (paymentDAO.addPayment(payment)) {
            AlertUtil.showSuccess("Success", "Payment Recorded", "Payment has been recorded successfully.");
            cancelPaymentForm();
            refreshPayments();
        } else {
            AlertUtil.showDatabaseError("record payment");
        }
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
            if (paymentDAO.updatePayment(payment)) {
                AlertUtil.showSuccess("Success", "Payment Refunded", "Payment has been refunded successfully.");
                refreshPayments();
            } else {
                AlertUtil.showDatabaseError("refund payment");
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
