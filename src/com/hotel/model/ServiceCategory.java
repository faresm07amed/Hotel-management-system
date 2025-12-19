package com.hotel.model;

public enum ServiceCategory {
    ROOM_SERVICE("Room Service"),
    LAUNDRY("Laundry"),
    SPA("Spa & Wellness"),
    TRANSPORT("Transportation"),
    MINIBAR("Minibar"),
    HOUSEKEEPING("Housekeeping"),
    OTHER("Other Services");

    private final String displayName;

    ServiceCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
