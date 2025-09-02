package com.college.inventoryerp.model;

//package com.college.inventory.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class IssueRecord {
    private final IntegerProperty recordId;
    private final IntegerProperty equipmentId;
    private final IntegerProperty facultyId;
    private final IntegerProperty employeeId;
    private final ObjectProperty<LocalDate> issueDate;
    private final ObjectProperty<LocalDate> returnDate;
    private final StringProperty status;
    private final StringProperty notes;
    private final IntegerProperty quantity;
    private final IntegerProperty availableQty;
    private int quantityIssued;

    public int getQuantityIssued() { return quantityIssued; }
    public int issuedQuantityProperty(){ return quantityIssued; }
    public void setQuantityIssued(int quantityIssued) { this.quantityIssued = quantityIssued; }



    // Display properties (for UI)
    private final StringProperty equipmentName;
    private final StringProperty facultyName;
    private final StringProperty employeeName;

    public IssueRecord(IntegerProperty quantity) {
        this(0, 0, 0, 0, null, null, "issued", "", quantity, 0, "", "", "");
    }



    public IssueRecord(int recordId, int equipmentId, int facultyId, int employeeId,
                       LocalDate issueDate, LocalDate returnDate, String status, String notes, IntegerProperty quantity,int availableQty,
                       String equipmentName, String facultyName, String employeeName) {
        this.recordId = new SimpleIntegerProperty(recordId);
        this.equipmentId = new SimpleIntegerProperty(equipmentId);
        this.facultyId = new SimpleIntegerProperty(facultyId);
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.issueDate = new SimpleObjectProperty<>(issueDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
        this.status = new SimpleStringProperty(status);
        this.notes = new SimpleStringProperty(notes);
        this.quantity = quantity;
        this.availableQty = new SimpleIntegerProperty(availableQty);
        this.equipmentName = new SimpleStringProperty(equipmentName);
        this.facultyName = new SimpleStringProperty(facultyName);
        this.employeeName = new SimpleStringProperty(employeeName);
    }

    // Record ID Property
    public IntegerProperty recordIdProperty() { return recordId; }
    public int getRecordId() { return recordId.get(); }
    public void setRecordId(int recordId) { this.recordId.set(recordId); }

    // Equipment ID Property
    public IntegerProperty equipmentIdProperty() { return equipmentId; }
    public int getEquipmentId() { return equipmentId.get(); }
    public void setEquipmentId(int equipmentId) { this.equipmentId.set(equipmentId); }

    // Faculty ID Property
    public IntegerProperty facultyIdProperty() { return facultyId; }
    public int getFacultyId() { return facultyId.get(); }
    public void setFacultyId(int facultyId) { this.facultyId.set(facultyId); }

    // Employee ID Property
    public IntegerProperty employeeIdProperty() { return employeeId; }
    public int getEmployeeId() { return employeeId.get(); }
    public void setEmployeeId(int employeeId) { this.employeeId.set(employeeId); }

    // Issue Date Property
    public ObjectProperty<LocalDate> issueDateProperty() { return issueDate; }
    public LocalDate getIssueDate() { return issueDate.get(); }
    public void setIssueDate(LocalDate issueDate) { this.issueDate.set(issueDate); }

    // Return Date Property
    public ObjectProperty<LocalDate> returnDateProperty() { return returnDate; }
    public LocalDate getReturnDate() { return returnDate.get(); }
    public void setReturnDate(LocalDate returnDate) { this.returnDate.set(returnDate); }

    // Status Property
    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    // Notes Property
    public StringProperty notesProperty() { return notes; }
    public String getNotes() { return notes.get(); }
    public void setNotes(String notes) { this.notes.set(notes); }

    // Equipment Name Property (for display)
    public StringProperty equipmentNameProperty() { return equipmentName; }
    public String getEquipmentName() { return equipmentName.get(); }
    public void setEquipmentName(String equipmentName) { this.equipmentName.set(equipmentName); }

    // Faculty Name Property (for display)
    public StringProperty facultyNameProperty() { return facultyName; }
    public String getFacultyName() { return facultyName.get(); }
    public void setFacultyName(String facultyName) { this.facultyName.set(facultyName); }

    // Employee Name Property (for display)
    public StringProperty employeeNameProperty() { return employeeName; }
    public String getEmployeeName() { return employeeName.get(); }
    public void setEmployeeName(String employeeName) { this.employeeName.set(employeeName); }

    // Quantity Property
    public IntegerProperty quantityProperty() {return quantity;}
    public int getQuantity() {return quantity.get();}
    public void setQuantity(int quantity) {this.quantity.set(quantity);}

    // Available quantity property
    public IntegerProperty availableQuantityProperty(){ return availableQty;}
    public int getAvailableQty(){ return availableQty.get();}
    public void setAvailableQty(int availableQty){ this.availableQty.set(availableQty);}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IssueRecord that = (IssueRecord) obj;
        return getRecordId() == that.getRecordId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getRecordId());
    }

    public String getDsrNumber() {
        Equipment equipment = new Equipment();
       return equipment.getDsrNumber();
    }


//    public int getQuantity() { return quantity;
//    }
}

