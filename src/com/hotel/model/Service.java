package com.hotel.model;

import javafx.beans.property.*;

public class Service {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty price;
    private final ObjectProperty<ServiceCategory> category;
    private final BooleanProperty isActive;

    public Service() {
        this(0, "", "", 0.0, ServiceCategory.OTHER, true);
    }

    public Service(int id, String name, String description, double price,
            ServiceCategory category, boolean isActive) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleObjectProperty<>(category);
        this.isActive = new SimpleBooleanProperty(isActive);
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

    // Name Property
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
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

    // Price Property
    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    // Category Property
    public ServiceCategory getCategory() {
        return category.get();
    }

    public void setCategory(ServiceCategory category) {
        this.category.set(category);
    }

    public ObjectProperty<ServiceCategory> categoryProperty() {
        return category;
    }

    // Is Active Property
    public boolean isActive() {
        return isActive.get();
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    @Override
    public String toString() {
        return name.get() + " - $" + price.get();
    }
}
