package com.hotel.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Payment {
    private final IntegerProperty id;
    private final ObjectProperty<Reservation> reservation;
    private final DoubleProperty amount;
    private final ObjectProperty<PaymentMethod> paymentMethod;
    private final ObjectProperty<LocalDateTime> paymentDate;
    private final ObjectProperty<PaymentStatus> status;
    private final StringProperty transactionId;
    private final StringProperty notes;

    public Payment() {
        this(0, null, 0.0, PaymentMethod.CASH, LocalDateTime.now(),
                PaymentStatus.PENDING, "", "");
    }

    public Payment(int id, Reservation reservation, double amount,
            PaymentMethod paymentMethod, LocalDateTime paymentDate,
            PaymentStatus status, String transactionId, String notes) {
        this.id = new SimpleIntegerProperty(id);
        this.reservation = new SimpleObjectProperty<>(reservation);
        this.amount = new SimpleDoubleProperty(amount);
        this.paymentMethod = new SimpleObjectProperty<>(paymentMethod);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.status = new SimpleObjectProperty<>(status);
        this.transactionId = new SimpleStringProperty(transactionId);
        this.notes = new SimpleStringProperty(notes);
    }

    // ID Property
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // Reservation Property
    public Reservation getReservation() {
        return reservation.get();
    }

    public void setReservation(Reservation reservation) {
        this.reservation.set(reservation);
    }

    public ObjectProperty<Reservation> reservationProperty() {
        return reservation;
    }

    // Amount Property
    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    // Payment Method Property
    public PaymentMethod getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public ObjectProperty<PaymentMethod> paymentMethodProperty() {
        return paymentMethod;
    }

    // Payment Date Property
    public LocalDateTime getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public ObjectProperty<LocalDateTime> paymentDateProperty() {
        return paymentDate;
    }

    // Status Property
    public PaymentStatus getStatus() {
        return status.get();
    }

    public void setStatus(PaymentStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<PaymentStatus> statusProperty() {
        return status;
    }

    // Transaction ID Property
    public String getTransactionId() {
        return transactionId.get();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId.set(transactionId);
    }

    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    // Notes Property
    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public int getReservationId() {
        return reservation.get() != null ? reservation.get().getId() : 0;
    }

    @Override
    public String toString() {
        return "Payment #" + id.get() + " - $" + amount.get();
    }
}
