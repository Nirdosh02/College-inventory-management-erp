package com.college.inventoryerp.dao;

//package com.college.inventory.dao;

//import com.college.inventory.model.Faculty;
import com.college.inventoryerp.model.Faculty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class FacultyDAO {
    private final DatabaseConnection dbConnection;

    public FacultyDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public ObservableList<Faculty> getAllFaculty() {
        ObservableList<Faculty> facultyList = FXCollections.observableArrayList();
        String query = "SELECT * FROM faculty ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Faculty faculty = new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                facultyList.add(faculty);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all faculty: " + e.getMessage());
            e.printStackTrace();
        }
        return facultyList;
    }

    public boolean addFaculty(Faculty faculty) {
        String query = "INSERT INTO faculty (name, department, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, faculty.getName());
            pstmt.setString(2, faculty.getDepartment());
            pstmt.setString(3, faculty.getEmail());
            pstmt.setString(4, faculty.getPhone());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding faculty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFaculty(Faculty faculty) {
        String query = "UPDATE faculty SET name = ?, department = ?, email = ?, phone = ? WHERE faculty_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, faculty.getName());
            pstmt.setString(2, faculty.getDepartment());
            pstmt.setString(3, faculty.getEmail());
            pstmt.setString(4, faculty.getPhone());
            pstmt.setInt(5, faculty.getFacultyId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating faculty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFaculty(int facultyId) {
        String query = "DELETE FROM faculty WHERE faculty_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, facultyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting faculty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Faculty getFacultyById(int facultyId) {
        String query = "SELECT * FROM faculty WHERE faculty_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, facultyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Faculty(
                            rs.getInt("faculty_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching faculty by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmailExists(String email, int excludeId) {
        String query = "SELECT COUNT(*) FROM faculty WHERE email = ? AND faculty_id != ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<String> getAllDepartments() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT department FROM faculty WHERE department IS NOT NULL AND department != '' ORDER BY department";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String department = rs.getString("department");
                if (department != null && !department.trim().isEmpty()) {
                    departments.add(department.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching departments: " + e.getMessage());
            e.printStackTrace();
        }
        return departments;
    }

    public ObservableList<Faculty> getFacultyByDepartment(String department) {
        ObservableList<Faculty> facultyList = FXCollections.observableArrayList();
        String query = "SELECT * FROM faculty WHERE department = ? ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Faculty faculty = new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                facultyList.add(faculty);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching faculty by department: " + e.getMessage());
            e.printStackTrace();
        }
        return facultyList;
    }

}

