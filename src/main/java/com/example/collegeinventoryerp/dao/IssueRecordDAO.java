package com.example.collegeinventoryerp.dao;

//package com.college.inventory.dao;

import com.example.collegeinventoryerp.model.Equipment;
import com.example.collegeinventoryerp.model.IssueRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class IssueRecordDAO {
    private final DatabaseConnection dbConnection;
    private final EquipmentDAO equipmentDAO;

    public IssueRecordDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.equipmentDAO = new EquipmentDAO();
    }

    public ObservableList<IssueRecord> getAllIssueRecords() {
        ObservableList<IssueRecord> issueRecords = FXCollections.observableArrayList();
        String query = """
            SELECT ir.*, e.name as equipment_name, f.name as faculty_name, emp.name as employee_name
            FROM issue_records ir
            JOIN equipment e ON ir.equipment_id = e.equipment_id
            JOIN faculty f ON ir.faculty_id = f.faculty_id
            JOIN employees emp ON ir.employee_id = emp.employee_id
            ORDER BY ir.issue_date DESC
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                IssueRecord record = new IssueRecord(
                        rs.getInt("record_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("faculty_id"),
                        rs.getInt("employee_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getString("notes"),
                        rs.getString("equipment_name"),
                        rs.getString("faculty_name"),
                        rs.getString("employee_name")
                );
                issueRecords.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all issue records: " + e.getMessage());
            e.printStackTrace();
        }
        return issueRecords;
    }

    public ObservableList<IssueRecord> getCurrentlyIssuedRecords() {
        ObservableList<IssueRecord> issueRecords = FXCollections.observableArrayList();
        String query = """
            SELECT ir.*, e.name as equipment_name, e.serial_number, f.name as faculty_name, 
                   f.department, emp.name as employee_name,
                   DATEDIFF(CURDATE(), ir.issue_date) as days_issued
            FROM issue_records ir
            JOIN equipment e ON ir.equipment_id = e.equipment_id
            JOIN faculty f ON ir.faculty_id = f.faculty_id
            JOIN employees emp ON ir.employee_id = emp.employee_id
            WHERE ir.status = 'issued'
            ORDER BY ir.issue_date DESC
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                IssueRecord record = new IssueRecord(
                        rs.getInt("record_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("faculty_id"),
                        rs.getInt("employee_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        null,
                        rs.getString("status"),
                        rs.getString("notes"),
                        rs.getString("equipment_name") + " (" + rs.getString("serial_number") + ")",
                        rs.getString("faculty_name") + " (" + rs.getString("department") + ")",
                        rs.getString("employee_name")
                );
                issueRecords.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching currently issued records: " + e.getMessage());
            e.printStackTrace();
        }
        return issueRecords;
    }

//    public boolean issueEquipment(int equipmentId, int facultyId, int employeeId, LocalDate issueDate, String notes) {
//        Connection conn = null;
//        try {
//            conn = dbConnection.getConnection();
//            conn.setAutoCommit(false);
//
//            Equipment equipment = equipmentDAO.getEquipmentById(equipmentId);
//            if (equipment == null || !"available".equalsIgnoreCase(equipment.getStatus().trim())) {
//                conn.rollback();
//                return false;
//            }
//
//            // Insert issue record
//            String insertQuery = "INSERT INTO issue_records (equipment_id, faculty_id, employee_id, issue_date, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
//            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
//                pstmt.setInt(1, equipmentId);
//                pstmt.setInt(2, facultyId);
//                pstmt.setInt(3, employeeId);
//                pstmt.setDate(4, Date.valueOf(issueDate));
//                pstmt.setString(5, notes);
//                pstmt.executeUpdate();
//            }
//
//            // Update equipment status to 'issued'
//            String updateStatus = "UPDATE equipment SET status = 'issued' WHERE equipment_id = ?";
//            try (PreparedStatement pstmt = conn.prepareStatement(updateStatus)) {
//                pstmt.setInt(1, equipmentId);
//                pstmt.executeUpdate();
//            }
//
//            conn.commit();
//            return true;
//        } catch (SQLException e) {
//            try {
//                if (conn != null) {
//                    conn.rollback();
//                }
//            } catch (SQLException rollbackEx) {
//                System.err.println("Error during rollback: " + rollbackEx.getMessage());
//            }
//            System.err.println("Error issuing equipment: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.setAutoCommit(true);
//                }
//            } catch (SQLException e) {
//                System.err.println("Error resetting auto-commit: " + e.getMessage());
//            }
//        }
//    }

            public boolean issueEquipment(int equipmentId, int facultyId, int employeeId, LocalDate issueDate, String notes) {
                Connection conn = null;
                try {
                    conn = dbConnection.getConnection();
                    conn.setAutoCommit(false);

                    Equipment equipment = equipmentDAO.getEquipmentById(conn, equipmentId);
                    System.out.println("DEBUG: equipment=" + equipment + " status=" + (equipment != null ? equipment.getStatus() : "null"));

                    if (equipment == null || !"available".equalsIgnoreCase(equipment.getStatus().trim())) {
                        System.out.println("DEBUG: Equipment check failed");
                        conn.rollback();
                        return false;
                    }

                    // Insert issue record
                    String insertQuery = "INSERT INTO issue_records (equipment_id, faculty_id, employee_id, issue_date, notes) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                        pstmt.setInt(1, equipmentId);
                        pstmt.setInt(2, facultyId);
                        pstmt.setInt(3, employeeId);
                        pstmt.setDate(4, Date.valueOf(issueDate));
                        pstmt.setString(5, notes);
                        pstmt.executeUpdate();
                    }

                    // Update equipment status
                    String updateStatus = "UPDATE equipment SET status = 'issued' WHERE equipment_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateStatus)) {
                        pstmt.setInt(1, equipmentId);
                        pstmt.executeUpdate();
                    }

                    conn.commit();
                    System.out.println("DEBUG: Equipment issued successfully in DB");
                    return true;
                } catch (SQLException e) {
                    try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
                    System.err.println("ERROR issuing equipment: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                } finally {
                    try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
                }
            }



    public boolean returnEquipment(int recordId, LocalDate returnDate) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Get equipment ID from issue record
            int equipmentId = 0;
            String getEquipmentQuery = "SELECT equipment_id FROM issue_records WHERE record_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getEquipmentQuery)) {
                pstmt.setInt(1, recordId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        equipmentId = rs.getInt("equipment_id");
                    }
                }
            }

            if (equipmentId == 0) {
                conn.rollback();
                return false;
            }

            // Update issue record
            String updateQuery = "UPDATE issue_records SET return_date = ?, status = 'returned' WHERE record_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setDate(1, Date.valueOf(returnDate));
                pstmt.setInt(2, recordId);
                pstmt.executeUpdate();
            }

            // Update equipment status to 'available'
            if (!equipmentDAO.updateEquipmentStatus(conn, equipmentId, "available")) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error returning equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean isEquipmentCurrentlyIssued(int equipmentId) {
        String query = "SELECT COUNT(*) FROM issue_records WHERE equipment_id = ? AND status = 'issued'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking equipment issue status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

