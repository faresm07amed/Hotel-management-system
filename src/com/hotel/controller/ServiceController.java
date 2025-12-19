package com.hotel.controller;

import com.hotel.dao.ServiceDAO;
import com.hotel.model.Service;
import com.hotel.model.ServiceCategory;
import com.hotel.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ServiceController {
    @FXML
    private TableView<Service> servicesTable;
    @FXML
    private TableColumn<Service, Integer> idColumn;
    @FXML
    private TableColumn<Service, String> nameColumn;
    @FXML
    private TableColumn<Service, String> descriptionColumn;
    @FXML
    private TableColumn<Service, String> categoryColumn;
    @FXML
    private TableColumn<Service, Double> priceColumn;
    @FXML
    private TableColumn<Service, Boolean> activeColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<ServiceCategory> categoryFilter;
    @FXML
    private CheckBox activeOnlyCheckbox;

    @FXML
    private VBox serviceFormPanel;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ServiceCategory> categoryComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private CheckBox isActiveCheckbox;

    private ServiceDAO serviceDAO = new ServiceDAO();
    private Service selectedService = null;
    private ObservableList<Service> allServices;

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        categoryColumn.setCellValueFactory(cellData -> {
            ServiceCategory category = cellData.getValue().getCategory();
            return new javafx.beans.property.SimpleStringProperty(category.getDisplayName());
        });
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<Service, Double>() {
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

        // Format active status column
        activeColumn.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty());
        activeColumn.setCellFactory(column -> new TableCell<Service, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(active ? "Active" : "Inactive");
                    if (active) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Populate category dropdowns
        categoryComboBox.setItems(FXCollections.observableArrayList(ServiceCategory.values()));

        // Add "All Categories" option to filter
        ObservableList<ServiceCategory> filterOptions = FXCollections.observableArrayList(ServiceCategory.values());
        categoryFilter.setItems(filterOptions);

        try {
            refreshServices();
        } catch (Exception e) {
            System.err.println("Warning: Could not load services from database: " + e.getMessage());
            // View will load anyway, just with empty table
        }
    }

    @FXML
    private void refreshServices() {
        if (activeOnlyCheckbox.isSelected()) {
            allServices = serviceDAO.getActiveServices();
        } else {
            allServices = serviceDAO.getAllServices();
        }
        servicesTable.setItems(allServices);
    }

    @FXML
    private void searchServices() {
        String term = searchField.getText().toLowerCase().trim();
        if (term.isEmpty()) {
            servicesTable.setItems(allServices);
            return;
        }

        ObservableList<Service> filteredServices = FXCollections.observableArrayList();
        for (Service service : allServices) {
            if (service.getName().toLowerCase().contains(term) ||
                    service.getDescription().toLowerCase().contains(term) ||
                    service.getCategory().getDisplayName().toLowerCase().contains(term)) {
                filteredServices.add(service);
            }
        }
        servicesTable.setItems(filteredServices);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        categoryFilter.setValue(null);
        refreshServices();
    }

    @FXML
    private void filterByCategory() {
        ServiceCategory selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null) {
            servicesTable.setItems(allServices);
            return;
        }

        ObservableList<Service> filteredServices = serviceDAO.getServicesByCategory(selectedCategory);
        servicesTable.setItems(filteredServices);
    }

    @FXML
    private void showAddServiceForm() {
        selectedService = null;
        clearForm();
        serviceFormPanel.setVisible(true);
        serviceFormPanel.setManaged(true);
    }

    @FXML
    private void editService() {
        selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            AlertUtil.showWarning("No Selection", "No Service Selected", "Please select a service to edit.");
            return;
        }

        nameField.setText(selectedService.getName());
        descriptionArea.setText(selectedService.getDescription());
        categoryComboBox.setValue(selectedService.getCategory());
        priceField.setText(String.valueOf(selectedService.getPrice()));
        isActiveCheckbox.setSelected(selectedService.isActive());

        serviceFormPanel.setVisible(true);
        serviceFormPanel.setManaged(true);
    }

    @FXML
    private void saveService() {
        // Validate
        String error = validate();
        if (error != null) {
            AlertUtil.showValidationError(error);
            return;
        }

        double price = Double.parseDouble(priceField.getText());

        if (selectedService == null) {
            // Add new service
            Service service = new Service(0,
                    nameField.getText().trim(),
                    descriptionArea.getText().trim(),
                    price,
                    categoryComboBox.getValue(),
                    isActiveCheckbox.isSelected());

            if (serviceDAO.addService(service)) {
                AlertUtil.showSuccess("Success", "Service Added", "Service has been added successfully.");
                cancelServiceForm();
                refreshServices();
            } else {
                AlertUtil.showDatabaseError("add service");
            }
        } else {
            // Update existing service
            selectedService.setName(nameField.getText().trim());
            selectedService.setDescription(descriptionArea.getText().trim());
            selectedService.setCategory(categoryComboBox.getValue());
            selectedService.setPrice(price);
            selectedService.setActive(isActiveCheckbox.isSelected());

            if (serviceDAO.updateService(selectedService)) {
                AlertUtil.showSuccess("Success", "Service Updated", "Service has been updated successfully.");
                cancelServiceForm();
                refreshServices();
            } else {
                AlertUtil.showDatabaseError("update service");
            }
        }
    }

    @FXML
    private void deleteService() {
        Service service = servicesTable.getSelectionModel().getSelectedItem();
        if (service == null) {
            AlertUtil.showWarning("No Selection", "No Service Selected", "Please select a service to delete.");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Delete Service",
                "Are you sure you want to delete '" + service.getName() + "'?")) {
            if (serviceDAO.deleteService(service.getId())) {
                AlertUtil.showSuccess("Success", "Service Deleted", "Service has been deleted successfully.");
                refreshServices();
            } else {
                AlertUtil.showDatabaseError("delete service");
            }
        }
    }

    @FXML
    private void toggleServiceStatus() {
        Service service = servicesTable.getSelectionModel().getSelectedItem();
        if (service == null) {
            AlertUtil.showWarning("No Selection", "No Service Selected",
                    "Please select a service to toggle its status.");
            return;
        }

        service.setActive(!service.isActive());
        if (serviceDAO.updateService(service)) {
            String status = service.isActive() ? "activated" : "deactivated";
            AlertUtil.showSuccess("Success", "Status Updated", "Service has been " + status + ".");
            refreshServices();
        } else {
            AlertUtil.showDatabaseError("update service status");
        }
    }

    @FXML
    private void cancelServiceForm() {
        serviceFormPanel.setVisible(false);
        serviceFormPanel.setManaged(false);
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        categoryComboBox.setValue(null);
        priceField.clear();
        isActiveCheckbox.setSelected(true);
    }

    private String validate() {
        String error;

        error = ValidationUtil.getRequiredFieldError("Service Name", nameField.getText());
        if (error != null)
            return error;

        if (categoryComboBox.getValue() == null) {
            return "Please select a category.";
        }

        error = ValidationUtil.getRequiredFieldError("Price", priceField.getText());
        if (error != null)
            return error;

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                return "Price must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            return "Price must be a valid number.";
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
