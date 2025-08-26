package com.example.collegeinventoryerp.model;

//package com.college.inventory.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Equipment {
    private final IntegerProperty equipmentId;
    private final StringProperty name;
    private final StringProperty model;
    private final StringProperty serialNumber;
    private final ObjectProperty<LocalDate> purchaseDate;
    private final StringProperty status;
    private final StringProperty category;
    private final StringProperty brand;
    private final StringProperty description;

    public Equipment() {
        this(0, "", "", "", null, "available", "", "", "");
    }

    public Equipment(int equipmentId, String name, String model, String serialNumber,
                     LocalDate purchaseDate, String status, String category, String brand, String description) {
        this.equipmentId = new SimpleIntegerProperty(equipmentId);
        this.name = new SimpleStringProperty(name);
        this.model = new SimpleStringProperty(model);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.purchaseDate = new SimpleObjectProperty<>(purchaseDate);
        this.status = new SimpleStringProperty(status);
        this.category = new SimpleStringProperty(category);
        this.brand = new SimpleStringProperty(brand);
        this.description = new SimpleStringProperty(description);
    }

    // Equipment ID Property
    public IntegerProperty equipmentIdProperty() { return equipmentId; }
    public int getEquipmentId() { return equipmentId.get(); }
    public void setEquipmentId(int equipmentId) { this.equipmentId.set(equipmentId); }

    // Name Property
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    // Model Property
    public StringProperty modelProperty() { return model; }
    public String getModel() { return model.get(); }
    public void setModel(String model) { this.model.set(model); }

    // Serial Number Property
    public StringProperty serialNumberProperty() { return serialNumber; }
    public String getSerialNumber() { return serialNumber.get(); }
    public void setSerialNumber(String serialNumber) { this.serialNumber.set(serialNumber); }

    // Purchase Date Property
    public ObjectProperty<LocalDate> purchaseDateProperty() { return purchaseDate; }
    public LocalDate getPurchaseDate() { return purchaseDate.get(); }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate.set(purchaseDate); }

    // Status Property
    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    // Category Property
    public StringProperty categoryProperty() { return category; }
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }

    // Brand Property
    public StringProperty brandProperty() { return brand; }
    public String getBrand() { return brand.get(); }
    public void setBrand(String brand) { this.brand.set(brand); }

    // Description Property
    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    @Override
    public String toString() {
        return name.get() + " (" + serialNumber.get() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Equipment equipment = (Equipment) obj;
        return getEquipmentId() == equipment.getEquipmentId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getEquipmentId());
    }
}

