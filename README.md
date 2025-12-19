# Hotel Management System - JavaFX MVC Project

## Overview
This is a comprehensive Hotel Management System built with JavaFX using MVC architecture. It includes guest management, room management, reservations, payments, and service/amenities management with MySQL database integration.

## Project Structure

```
HMS/
├── pom.xml                     # Maven configuration
├── database_schema.sql         # MySQL database schema
├── src/
│   ├── module-info.java        # JavaFX module configuration
│   └── com/hotel/
│       ├── HotelManagementApp.java          # Main application entry point
│       ├── config/
│       │   └── DatabaseConfig.java          # Database connection settings
│       ├── model/                           # Model entities with JavaFX properties
│       │   ├── Guest.java
│       │   ├── Room.java
│       │   ├── Reservation.java
│       │   ├── Payment.java
│       │   ├── Service.java
│       │   ├── ReservationService.java
│       │   ├── RoomType.java                # Enum
│       │   ├── RoomStatus.java              # Enum
│       │   ├── ReservationStatus.java       # Enum
│       │   ├── PaymentMethod.java           # Enum
│       │   ├── PaymentStatus.java           # Enum
│       │   └── ServiceCategory.java         # Enum
│       ├── view/                            # FXML views
│       │   ├── styles.css
│       │   ├── dashboard.fxml               # ✓ Created
│       │   ├── guests.fxml                  # ✓ Created
│       │   ├── rooms.fxml                   # TODO
│       │   ├── reservations.fxml            # TODO
│       │   ├── payments.fxml                # TODO
│       │   └── services.fxml                # TODO
│       ├── controller/                      # Controllers
│       │   ├── DashboardController.java     # ✓ Created
│       │   ├── GuestController.java         # TODO
│       │   ├── RoomController.java          # TODO
│       │   ├── ReservationController.java   # TODO
│       │   ├── PaymentController.java       # TODO
│       │   └── ServiceController.java       # TODO
│       ├── dao/                             # Data Access Objects
│       │   ├── GuestDAO.java                # ✓ Created
│       │   ├── RoomDAO.java                 # ✓ Created
│       │   ├── ReservationDAO.java          # ✓ Created
│       │   ├── PaymentDAO.java              # ✓ Created
│       │   ├── ServiceDAO.java              # ✓ Created
│       │   └── ReservationServiceDAO.java   # ✓ Created
│       ├── service/
│       │   └── DatabaseManager.java         # ✓ Created
│       └── util/
│           ├── ValidationUtil.java          # ✓ Created
│           ├── NavigationUtil.java          # ✓ Created
│           ├── AlertUtil.java               # ✓ Created
│           └── ReceiptGenerator.java        # ✓ Created
```

## Setup Instructions

### 1. Database Setup
```bash
# Start MySQL server
# Create the database and run the schema
mysql -u root -p
CREATE DATABASE hotel_management;
USE hotel_management;
SOURCE database_schema.sql;
```

### 2. Configure Database Connection
Edit `src/com/hotel/config/DatabaseConfig.java`:
```java
private static final String DB_USER = "root";        // Your MySQL username
private static final String DB_PASSWORD = "password"; //  Your MySQL password
```

### 3. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn javafx:run
```

## Completing the Remaining Controllers

I've created the foundational code. Here's how to complete the remaining views and controllers:

### GuestController.java Template

```java
package com.hotel.controller;

import com.hotel.dao.GuestDAO;
import com.hotel.model.Guest;
import com.hotel.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class GuestController {
    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, Integer> idColumn;
    @FXML private TableColumn<Guest, String> firstNameColumn;
    @FXML private TableColumn<Guest, String> lastNameColumn;
    @FXML private TableColumn<Guest, String> emailColumn;
    @FXML private TableColumn<Guest, String> phoneColumn;
    @FXML private TableColumn<Guest, String> idNumberColumn;
    @FXML private TableColumn<Guest, String> addressColumn;
    
    @FXML private TextField searchField;
    @FXML private VBox guestFormPanel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField idNumberField;
    @FXML private TextArea addressArea;
    
    private GuestDAO guestDAO = new GuestDAO();
    private Guest selectedGuest = null;

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        idNumberColumn.setCellValueFactory(cellData -> cellData.getValue().idNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        
        refreshGuests();
    }

    @FXML private void refreshGuests() {
        guestsTable.setItems(guestDAO.getAllGuests());
    }

    @FXML private void searchGuests() {
        String term = searchField.getText();
        if (term != null && !term.isEmpty()) {
            guestsTable.setItems(guestDAO.searchGuests(term));
        } else {
            refreshGuests();
        }
    }

    @FXML private void clearSearch() {
        searchField.clear();
        refreshGuests();
    }

    @FXML private void showAddGuestForm() {
        selectedGuest = null;
        clearForm();
        guestFormPanel.setVisible(true);
        guestFormPanel.setManaged(true);
    }

    @FXML private void editGuest() {
        selectedGuest = guestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest == null) {
            AlertUtil.showWarning("No Selection", "No Guest Selected", "Please select a guest to edit.");
            return;
        }
        
        firstNameField.setText(selectedGuest.getFirstName());
        lastNameField.setText(selectedGuest.getLastName());
        emailField.setText(selectedGuest.getEmail());
        phoneField.setText(selectedGuest.getPhone());
        idNumberField.setText(selectedGuest.getIdNumber());
        addressArea.setText(selectedGuest.getAddress());
        
        guestFormPanel.setVisible(true);
        guestFormPanel.setManaged(true);
    }

    @FXML private void saveGuest() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }
        
        if (selectedGuest == null) {
            // Add new guest
            Guest guest = new Guest(0, 
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                idNumberField.getText(),
                addressArea.getText()
            );
            
            if (guestDAO.addGuest(guest)) {
                AlertUtil.showSuccess("Success", "Guest Added", "Guest has been added successfully.");
                cancelGuestForm();
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("add guest");
            }
        } else {
            // Update existing guest
            selectedGuest.setFirstName(firstNameField.getText());
            selectedGuest.setLastName(lastNameField.getText());
            selectedGuest.setEmail(emailField.getText());
            selectedGuest.setPhone(phoneField.getText());
            selectedGuest.setIdNumber(idNumberField.getText());
            selectedGuest.setAddress(addressArea.getText());
            
            if (guestDAO.updateGuest(selectedGuest)) {
                AlertUtil.showSuccess("Success", "Guest Updated", "Guest has been updated successfully.");
                cancelGuestForm();
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("update guest");
            }
        }
    }

    @FXML private void deleteGuest() {
        Guest guest = guestsTable.getSelectionModel().getSelectedItem();
        if (guest == null) {
            AlertUtil.showWarning("No Selection", "No Guest Selected", "Please select a guest to delete.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Confirm Delete", "Delete Guest", 
            "Are you sure you want to delete " + guest.getFullName() + "?")) {
            if (guestDAO.deleteGuest(guest.getId())) {
                AlertUtil.showSuccess("Success", "Guest Deleted", "Guest has been deleted successfully.");
                refreshGuests();
            } else {
                AlertUtil.showDatabaseError("delete guest");
            }
        }
    }

    @FXML private void cancelGuestForm() {
        guestFormPanel.setVisible(false);
        guestFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        idNumberField.clear();
        addressArea.clear();
    }

    private String validate() {
        String error;
        error = ValidationUtil.getRequiredFieldError("First Name", firstNameField.getText());
        if (error != null) return error;
        
        error = ValidationUtil.getRequiredFieldError("Last Name", lastNameField.getText());
        if (error != null) return error;
        
        error = ValidationUtil.getEmailError(emailField.getText());
        if (error != null) return error;
        
        error = ValidationUtil.getPhoneError(phoneField.getText());
        if (error != null) return error;
        
        error = ValidationUtil.getRequiredFieldError("ID Number", idNumberField.getText());
        if (error != null) return error;
        
        return null;
    }

    // Navigation methods
    @FXML private void goToDashboard() { NavigationUtil.loadDashboard(); }
    @FXML private void goToGuests() { NavigationUtil.loadGuests(); }
    @FXML private void goToRooms() { NavigationUtil.loadRooms(); }
    @FXML private void goToReservations() { NavigationUtil.loadReservations(); }
    @FXML private void goToPayments() { NavigationUtil.loadPayments(); }
    @FXML private void goToServices() { NavigationUtil.loadServices(); }
}
```

## Features

### ✓ Completed
- **Database Layer**: MySQL schema with all tables, relationships, and sample data
- **Configuration**: Database configuration and connection management
- **Model Layer**: All entities with JavaFX properties (Guest, Room, Reservation, Payment, Service, ReservationService)
- **DAO Layer**: Complete CRUD operations for all entities
- **Utilities**: Validation, navigation, alerts, and receipt generation
- **Main Application**: JavaFX entry point
- **Dashboard**: Statistics view with navigation
- **Styling**: Modern CSS with card-based design

### TODO (Follow the pattern above)
- **GuestController**: Implement using template above
- **RoomController**: Similar to GuestController, manage rooms with type and status
- **ReservationController**: Handle bookings, check-in/out, date validation
- **PaymentController**: Record payments, generate receipts
- **ServiceController**: Manage services and assign to reservations

## Key Features

1. **Guest Management**: Add, edit, delete, search guests
2. **Room Management**: Manage rooms by type and status
3. **Reservation System**: Book rooms with conflict detection
4. **Payment Processing**: Multiple payment methods, receipt generation
5. **Service Management**: Assign amenities to reservations
6. **Real-time Dashboard**: Statistics and quick actions

## Technologies Used
- Java 11+
- JavaFX 17
- MySQL 8.0+
- Maven
- JDBC

## Database Configuration Note
Make sure to update `DatabaseConfig.java` with your MySQL credentials before running the application.

## Running the Application
1. Ensure MySQL is running and database is created
2. Update database credentials in DatabaseConfig.java
3. Run `mvn javafx:run`
4. The dashboard will open automatically

## Next Steps
1. Complete the remaining controllers (Guest, Room, Reservation, Payment, Service)
2. Complete the remaining FXML views
3. Test all CRUD operations
4. Add error handling and logging
5. Add data validation
6. Test with real data

The foundation is complete - you just need to replicate the pattern shown in GuestController for the other controllers!
