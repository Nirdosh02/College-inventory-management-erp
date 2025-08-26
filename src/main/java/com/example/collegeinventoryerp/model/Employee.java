package com.example.collegeinventoryerp.model;
//package com.college.inventory.model;

import javafx.beans.property.*;

public class Employee {
    private final IntegerProperty employeeId;
    private final StringProperty name;
    private final StringProperty designation;
    private final StringProperty email;
    private final StringProperty phone;

    public Employee() {
        this(0, "", "", "", "");
    }

    public Employee(int employeeId, String name, String designation, String email, String phone) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.name = new SimpleStringProperty(name);
        this.designation = new SimpleStringProperty(designation);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }

    // Employee ID Property
    public IntegerProperty employeeIdProperty() { return employeeId; }
    public int getEmployeeId() { return employeeId.get(); }
    public void setEmployeeId(int employeeId) { this.employeeId.set(employeeId); }

    // Name Property
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    // Designation Property
    public StringProperty designationProperty() { return designation; }
    public String getDesignation() { return designation.get(); }
    public void setDesignation(String designation) { this.designation.set(designation); }

    // Email Property
    public StringProperty emailProperty() { return email; }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    // Phone Property
    public StringProperty phoneProperty() { return phone; }
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }

    @Override
    public String toString() {
        return name.get() + " (" + designation.get() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return getEmployeeId() == employee.getEmployeeId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getEmployeeId());
    }
}
