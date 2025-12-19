package com.hotel.model;

import javafx.beans.property.*;

public class Guest {
    private final IntegerProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty idNumber;
    private final StringProperty address;

    public Guest() {
        this(0, "", "", "", "", "", "");
    }

    public Guest(int id, String firstName, String lastName, String email,
            String phone, String idNumber, String address) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.idNumber = new SimpleStringProperty(idNumber);
        this.address = new SimpleStringProperty(address);
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

    // First Name Property
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    // Last Name Property
    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    // Email Property
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // Phone Property
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    // ID Number Property
    public String getIdNumber() {
        return idNumber.get();
    }

    public void setIdNumber(String idNumber) {
        this.idNumber.set(idNumber);
    }

    public StringProperty idNumberProperty() {
        return idNumber;
    }

    // Address Property
    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
