package com.example.collegeinventoryerp.model;

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
    private final StringProperty description;
    private final IntegerProperty quantity;
    private final StringProperty dsrNumber;
    private final IntegerProperty issuedQuantity;




    public Equipment(IntegerProperty issuedQuantity) {
        this(0, "", "", "", null, "available", "", "",0, 0, "");
    }
    // Default constructor for new equipment creation
    public Equipment() {
        this.equipmentId = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.model = new SimpleStringProperty("");
        this.serialNumber = new SimpleStringProperty("");
        this.purchaseDate = new SimpleObjectProperty<>(null);
        this.status = new SimpleStringProperty("available");
        this.category = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.quantity = new SimpleIntegerProperty(0);
        this.issuedQuantity = new SimpleIntegerProperty(0);
        this.dsrNumber = new SimpleStringProperty("");
    }


//    public Equipment(int equipmentId, String name, String model, String serialNumber,
//                     LocalDate purchaseDate, String status, String category, String description, int quantity, String dsrNumber, IntegerProperty issuedQuantity) {
//        this.equipmentId = new SimpleIntegerProperty(equipmentId);
//        this.name = new SimpleStringProperty(name);
//        this.model = new SimpleStringProperty(model);
//        this.serialNumber = new SimpleStringProperty(serialNumber);
//        this.purchaseDate = new SimpleObjectProperty<>(purchaseDate);
//        this.status = new SimpleStringProperty(status);
//        this.category = new SimpleStringProperty(category);
//       this.brand = new SimpleStringProperty(brand);
//        this.description = new SimpleStringProperty(description);
//        this.quantity = new SimpleIntegerProperty(quantity);
//        this.dsrNumber = new SimpleStringProperty(dsrNumber);
//        this.issuedQuantity = issuedQuantity;
//    }
    public Equipment(int equipmentId, String name, String model, String serialNumber,
                     LocalDate purchaseDate, String status, String category, String description,
                     int quantity, int issuedQuantity, String dsrNumber) {
        this.equipmentId = new SimpleIntegerProperty(equipmentId);
        this.name = new SimpleStringProperty(name);
        this.model = new SimpleStringProperty(model);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.purchaseDate = new SimpleObjectProperty<>(purchaseDate);
        this.status = new SimpleStringProperty(status);
        this.category = new SimpleStringProperty(category);
        this.description = new SimpleStringProperty(description);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.issuedQuantity = new SimpleIntegerProperty(issuedQuantity); // Properly initialize
        this.dsrNumber = new SimpleStringProperty(dsrNumber);
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
//    public StringProperty brandProperty() { return brand; }
//    public String getBrand() { return brand.get(); }
//    public void setBrand(String brand) { this.brand.set(brand); }

    // Description Property
    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    // Quantity property


    public String getDsrNumber() { return dsrNumber.get(); }
    public StringProperty dsrNumberProperty() { return dsrNumber; }
    public void setDsrNumber(String dsrNumber){ this.dsrNumber.set(dsrNumber);}

    public int getQuantity() { return quantity.get(); }
    public IntegerProperty quantityProperty() { return quantity; }
    public void setQuantity(int quantity){this.quantity.set(quantity);}

    public IntegerProperty issuedQuantityProperty() { return issuedQuantity; }
    public int getIssuedQuantity() { return issuedQuantity.get(); }
    public void setIssuedQuantity(int issuedQuantity){ this.issuedQuantity.set(issuedQuantity);}

    public int getAvailableQuantity() {
        return getQuantity() - getIssuedQuantity();
    }

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

