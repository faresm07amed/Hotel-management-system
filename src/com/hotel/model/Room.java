package com.hotel.model;

import javafx.beans.property.*;

public class Room {
    private final StringProperty roomNumber;
    private final ObjectProperty<RoomType> type;
    private final ObjectProperty<RoomStatus> status;
    private final DoubleProperty pricePerNight;
    private final StringProperty description;
    private final IntegerProperty maxOccupancy;

    public Room() {
        this("", RoomType.SINGLE, RoomStatus.AVAILABLE, 0.0, "", 1);
    }

    public Room(String roomNumber, RoomType type, RoomStatus status,
            double pricePerNight, String description, int maxOccupancy) {
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.type = new SimpleObjectProperty<>(type);
        this.status = new SimpleObjectProperty<>(status);
        this.pricePerNight = new SimpleDoubleProperty(pricePerNight);
        this.description = new SimpleStringProperty(description);
        this.maxOccupancy = new SimpleIntegerProperty(maxOccupancy);
    }

    // Room Number Property
    public String getRoomNumber() {
        return roomNumber.get();
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber.set(roomNumber);
    }

    public StringProperty roomNumberProperty() {
        return roomNumber;
    }

    // Type Property
    public RoomType getType() {
        return type.get();
    }

    public void setType(RoomType type) {
        this.type.set(type);
    }

    public ObjectProperty<RoomType> typeProperty() {
        return type;
    }

    // Status Property
    public RoomStatus getStatus() {
        return status.get();
    }

    public void setStatus(RoomStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<RoomStatus> statusProperty() {
        return status;
    }

    // Price Per Night Property
    public double getPricePerNight() {
        return pricePerNight.get();
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight.set(pricePerNight);
    }

    public DoubleProperty pricePerNightProperty() {
        return pricePerNight;
    }

    // Description Property
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    // Max Occupancy Property
    public int getMaxOccupancy() {
        return maxOccupancy.get();
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy.set(maxOccupancy);
    }

    public IntegerProperty maxOccupancyProperty() {
        return maxOccupancy;
    }

    @Override
    public String toString() {
        return roomNumber.get() + " - " + type.get();
    }
}
