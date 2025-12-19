package com.hotel.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class ReservationService {
    private final IntegerProperty id;
    private final ObjectProperty<Reservation> reservation;
    private final ObjectProperty<Service> service;
    private final IntegerProperty quantity;
    private final ObjectProperty<LocalDateTime> dateRequested;
    private final StringProperty status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private final DoubleProperty totalPrice;
    private final StringProperty notes;

    public ReservationService() {
        this(0, null, null, 1, LocalDateTime.now(), "PENDING", 0.0, "");
    }

    public ReservationService(int id, Reservation reservation, Service service,
            int quantity, LocalDateTime dateRequested, String status,
            double totalPrice, String notes) {
        this.id = new SimpleIntegerProperty(id);
        this.reservation = new SimpleObjectProperty<>(reservation);
        this.service = new SimpleObjectProperty<>(service);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.dateRequested = new SimpleObjectProperty<>(dateRequested);
        this.status = new SimpleStringProperty(status);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
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

    // Service Property
    public Service getService() {
        return service.get();
    }

    public void setService(Service service) {
        this.service.set(service);
    }

    public ObjectProperty<Service> serviceProperty() {
        return service;
    }

    // Quantity Property
    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    // Date Requested Property
    public LocalDateTime getDateRequested() {
        return dateRequested.get();
    }

    public void setDateRequested(LocalDateTime dateRequested) {
        this.dateRequested.set(dateRequested);
    }

    public ObjectProperty<LocalDateTime> dateRequestedProperty() {
        return dateRequested;
    }

    // Status Property
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Total Price Property
    public double getTotalPrice() {
        return totalPrice.get();
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public DoubleProperty totalPriceProperty() {
        return totalPrice;
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

    public String getServiceName() {
        return service.get() != null ? service.get().getName() : "";
    }

    public int getReservationId() {
        return reservation.get() != null ? reservation.get().getId() : 0;
    }

    @Override
    public String toString() {
        return getServiceName() + " x" + quantity.get();
    }
}
