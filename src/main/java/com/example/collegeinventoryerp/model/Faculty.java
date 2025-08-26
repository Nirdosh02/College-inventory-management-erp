package com.example.collegeinventoryerp.model;

//package com.college.inventory.model;

import javafx.beans.property.*;

public class Faculty {
    private final IntegerProperty facultyId;
    private final StringProperty name;
    private final StringProperty department;
    private final StringProperty email;
    private final StringProperty phone;

    public Faculty() {
        this(0, "", "", "", "");
    }

    public Faculty(int facultyId, String name, String department, String email, String phone) {
        this.facultyId = new SimpleIntegerProperty(facultyId);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }

    // Faculty ID Property
    public IntegerProperty facultyIdProperty() { return facultyId; }
    public int getFacultyId() { return facultyId.get(); }
    public void setFacultyId(int facultyId) { this.facultyId.set(facultyId); }

    // Name Property
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    // Department Property
    public StringProperty departmentProperty() { return department; }
    public String getDepartment() { return department.get(); }
    public void setDepartment(String department) { this.department.set(department); }

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
        return name.get() + " (" + department.get() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Faculty faculty = (Faculty) obj;
        return getFacultyId() == faculty.getFacultyId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getFacultyId());
    }
}

