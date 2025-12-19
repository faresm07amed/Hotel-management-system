package com.hotel.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Reservation {
    private final IntegerProperty id;
    private final ObjectProperty<Guest> guest;
    private final ObjectProperty<Room> room;
    private final ObjectProperty<LocalDate> checkInDate;
    private final ObjectProperty<LocalDate> checkOutDate;
    private final ObjectProperty<ReservationStatus> status;
    private final DoubleProperty totalPrice;
    private final StringProperty notes;

    public Reservation() {
        this(0, null, null, LocalDate.now(), LocalDate.now().plusDays(1),
                ReservationStatus.PENDING, 0.0, "");
    }

    public Reservation(int id, Guest guest, Room room, LocalDate checkInDate,
            LocalDate checkOutDate, ReservationStatus status,
            double totalPrice, String notes) {
        this.id = new SimpleIntegerProperty(id);
        this.guest = new SimpleObjectProperty<>(guest);
        this.room = new SimpleObjectProperty<>(room);
        this.checkInDate = new SimpleObjectProperty<>(checkInDate);
        this.checkOutDate = new SimpleObjectProperty<>(checkOutDate);
        this.status = new SimpleObjectProperty<>(status);
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

    // Guest Property
    public Guest getGuest() {
        return guest.get();
    }

    public void setGuest(Guest guest) {
        this.guest.set(guest);
    }

    public ObjectProperty<Guest> guestProperty() {
        return guest;
    }

    // Room Property
    public Room getRoom() {
        return room.get();
    }

    public void setRoom(Room room) {
        this.room.set(room);
    }

    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    // Check-In Date Property
    public LocalDate getCheckInDate() {
        return checkInDate.get();
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate.set(checkInDate);
    }

    public ObjectProperty<LocalDate> checkInDateProperty() {
        return checkInDate;
    }

    // Check-Out Date Property
    public LocalDate getCheckOutDate() {
        return checkOutDate.get();
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate.set(checkOutDate);
    }

    public ObjectProperty<LocalDate> checkOutDateProperty() {
        return checkOutDate;
    }

    // Status Property
    public ReservationStatus getStatus() {
        return status.get();
    }

    public void setStatus(ReservationStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<ReservationStatus> statusProperty() {
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

    public String getGuestName() {
        return guest.get() != null ? guest.get().getFullName() : "";
    }

    public String getRoomNumber() {
        return room.get() != null ? room.get().getRoomNumber() : "";
    }

    @Override
    public String toString() {
        return "Reservation #" + id.get() + " - " + getGuestName();
    }
}
