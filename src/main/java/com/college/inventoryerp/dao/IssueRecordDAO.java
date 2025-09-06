package com.college.inventoryerp.dao;

//package com.college.inventory.dao;

import com.college.inventoryerp.model.Equipment;
import com.college.inventoryerp.model.IssueRecord;
import javafx.beans.property.SimpleIntegerProperty;
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
        SELECT ir.*, e.name as equipment_name, f.name as faculty_name, emp.name as employee_name,
        e.quantity as total_quantity,  -- ADD: Include total_quantity
        (e.quantity - e.issued_quantity) as available_quantity
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
                        new SimpleIntegerProperty(rs.getInt("quantity")),
                        rs.getInt("available_quantity"),
                        rs.getString("equipment_name"),
                        rs.getString("faculty_name"),
                        rs.getString("employee_name"),
                        rs.getInt("total_quantity")  // ADD: Pass total_quantity parameter
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
    SELECT ir.*, e.name as equipment_name, e.serial_number, 
           e.quantity as total_quantity,
           e.issued_quantity, f.name as faculty_name, 
           f.department, emp.name as employee_name,
           (e.quantity - e.issued_quantity) as available_quantity,
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
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getString("notes"),
                        new SimpleIntegerProperty(rs.getInt("quantity")),
                        rs.getInt("available_quantity"),
                        rs.getString("equipment_name"),
                        rs.getString("faculty_name"),
                        rs.getString("employee_name"),
                        rs.getInt("total_quantity")  // ADD: Pass total quantity
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

    public boolean issueEquipment(int equipmentId, int facultyId, int employeeId, LocalDate issueDate, String notes, int quantity) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            Equipment equipment = equipmentDAO.getEquipmentById(conn, equipmentId);
            System.out.println("DEBUG: equipment=" + equipment );

//            if (equipment == null || !"available".equalsIgnoreCase(equipment.getStatus().trim())) {
//                System.out.println("DEBUG: Equipment check failed");
//                conn.rollback();
//                return false;
//            }
            if (equipment == null) {
                System.out.println("DEBUG: Equipment not found");
                conn.rollback();
                return false;
            }
            int availableQty = equipment.getQuantity() - equipment.getIssuedQuantity();
            System.out.println("DEBUG: Total quantity: " + equipment.getQuantity() +
                    ", Issued quantity: " + equipment.getIssuedQuantity() +
                    ", Available: " + availableQty +
                    ", Requested: " + quantity);

            if (quantity > availableQty) {
                System.out.println("DEBUG: Not enough stock available");
                conn.rollback();
                return false;
            }

            // INSERT with quantity - ADD the quantity parameter here
            String insertQuery = "INSERT INTO issue_records (equipment_id, faculty_id, employee_id, issue_date, status, notes, quantity) VALUES (?, ?, ?, ?, 'issued', ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setInt(1, equipmentId);
                pstmt.setInt(2, facultyId);
                pstmt.setInt(3, employeeId);
                pstmt.setDate(4, Date.valueOf(issueDate));
                pstmt.setString(5, notes);
                pstmt.setInt(6, quantity); // ADD THIS LINE
                pstmt.executeUpdate();
            }

            // Update equipment quantities
            String updateQuery = "UPDATE equipment SET issued_quantity = issued_quantity + ? WHERE equipment_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, equipmentId);
                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("DEBUG: Rows updated: " + rowsUpdated);
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

    public boolean returnEquipment(int issueId, LocalDate returnDate, int equipmentId) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Get the quantity that was issued from the issue_records table
            String getQuantityQuery = "SELECT quantity, equipment_id FROM issue_records WHERE record_id = ? AND status = 'issued'";
            int issuedQuantity = 0;
            int dbEquipmentId = 0;

            try (PreparedStatement pstmt = conn.prepareStatement(getQuantityQuery)) {
                pstmt.setInt(1, issueId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        issuedQuantity = rs.getInt("quantity");
                        dbEquipmentId = rs.getInt("equipment_id");
                        System.out.println("DEBUG: Found issue record - quantity=" + issuedQuantity +
                                ", equipmentId=" + dbEquipmentId);
                    } else {
                        System.out.println("DEBUG: No active issue record found for ID: " + issueId);
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Update the issue record status and return date
            String updateIssueQuery = "UPDATE issue_records SET return_date = ?, status = 'returned' WHERE record_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateIssueQuery)) {
                pstmt.setDate(1, Date.valueOf(returnDate));
                pstmt.setInt(2, issueId);
                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("DEBUG: Issue record updated. Rows affected: " + rowsUpdated);
            }

            // Decrease the issued_quantity in equipment table
            String updateEquipmentQuery = "UPDATE equipment SET issued_quantity = issued_quantity - ? WHERE equipment_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateEquipmentQuery)) {
                pstmt.setInt(1, issuedQuantity);
                pstmt.setInt(2, equipmentId);
                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("DEBUG: Equipment updated. Decreased issued_quantity by " + issuedQuantity +
                        ". Rows affected: " + rowsUpdated);
            }

            conn.commit();
            System.out.println("DEBUG: Equipment return completed successfully");
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
                System.out.println("DEBUG: Transaction rolled back due to error");
            } catch (SQLException ignored) {}
            System.err.println("ERROR returning equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }




    //    public boolean returnEquipment(int issueId, LocalDate returnDate, int qty, int equipmentId) {
//        String updateIssue = "UPDATE issue_records SET return_date = ?, status = 'returned' WHERE record_id = ?";
//        String updateEquipment = "UPDATE equipment SET issued_quantity = issued_quantity - ? WHERE equipment_id = ?";
//
//        try (Connection conn = dbConnection.getConnection()) {
//            conn.setAutoCommit(false);
//
//            // Mark as returned
//            try (PreparedStatement pstmt = conn.prepareStatement(updateIssue)) {
//                pstmt.setDate(1, Date.valueOf(returnDate));
//                pstmt.setInt(2, issueId);
//                pstmt.executeUpdate();
//            }
//
//            // Update stock
//            try (PreparedStatement pstmt = conn.prepareStatement(updateEquipment)) {
//                pstmt.setInt(1, qty);
//                pstmt.setInt(2, equipmentId);
//                pstmt.executeUpdate();
//            }
//
//            conn.commit();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public int getAvailableQuantity(int equipmentId){
        String query = "SELECT quantity, issued_quantity FROM equipment WHERE equipment_id = ? ";
        try(Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    int totalQty = rs.getInt("quantity");
                    int issuedQty =  rs.getInt("issued_quantity");
                    return totalQty-issuedQty;
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting available quantity: " + e.getMessage());
            e.printStackTrace();;
        }
        return 0;
    }


}

