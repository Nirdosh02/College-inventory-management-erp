package com.college.inventoryerp.dao;

//package com.college.inventory.dao;

import com.college.inventoryerp.model.Equipment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class EquipmentDAO {
    private final DatabaseConnection dbConnection;

    public EquipmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public ObservableList<Equipment> getAllEquipment() {
        ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();
        String query = "SELECT * FROM equipment ORDER BY equipment_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Equipment equipment = new Equipment(
                        rs.getInt("equipment_id"),
                        rs.getString("name"),
                        rs.getString("model"),
                        rs.getString("serial_number"),
                        rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getInt("issued_quantity"),
                        rs.getString("dsr_number")
                );
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all equipment: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    public ObservableList<Equipment> getAvailableEquipment() {
        ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();
        String query = "SELECT * FROM equipment WHERE (quantity - COALESCE(issued_quantity, 0)) > 0 ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Equipment equipment = new Equipment(
                        rs.getInt("equipment_id"),
                        rs.getString("name"),
                        rs.getString("model"),
                        rs.getString("serial_number"),
                        rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getInt("issued_quantity"),
                        rs.getString("dsr_number")
                );
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available equipment: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    public boolean addEquipment(Equipment equipment) {
        String query = "INSERT INTO equipment (name, model, serial_number, purchase_date, status, category, description, quantity, dsr_number, issued_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getModel());
            pstmt.setString(3, equipment.getSerialNumber());
            pstmt.setDate(4, equipment.getPurchaseDate() != null ? Date.valueOf(equipment.getPurchaseDate()) : null);
            pstmt.setString(5, equipment.getStatus());
            pstmt.setString(6, equipment.getCategory());
            pstmt.setString(7, equipment.getDescription());
            pstmt.setInt(8, equipment.getQuantity());
            pstmt.setString(9, equipment.getDsrNumber());
            pstmt.setInt(10, 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEquipment(Equipment equipment) {
        String query = "UPDATE equipment SET name = ?, model = ?, serial_number = ?, purchase_date = ?, status = ?, category = ?, description = ?, quantity = ?, dsr_number = ?, issued_quantity = ? WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getModel());
            pstmt.setString(3, equipment.getSerialNumber());
            pstmt.setDate(4, equipment.getPurchaseDate() != null ? Date.valueOf(equipment.getPurchaseDate()) : null);
            pstmt.setString(5, equipment.getStatus());
            pstmt.setString(6, equipment.getCategory());
            pstmt.setString(7, equipment.getDescription());
            pstmt.setInt(8, equipment.getQuantity());
            pstmt.setString(9, equipment.getDsrNumber());
            pstmt.setInt(10, equipment.getIssuedQuantity());
            pstmt.setInt(11, equipment.getEquipmentId());


            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean issueEquipment(int equipmentId, int issueCount) {
        String query = "UPDATE equipment SET quantity = quantity - ?, issued_quantity = issued_quantity + ? WHERE equipment_id = ? AND quantity >= ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, issueCount);
            pstmt.setInt(2, issueCount);
            pstmt.setInt(3, equipmentId);
            pstmt.setInt(4, issueCount); // ensure we don’t issue more than available

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error issuing equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteEquipment(int equipmentId) {
        String query = "DELETE FROM equipment WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, equipmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//    public Equipment getEquipmentById(int equipmentId) {
//        String query = "SELECT * FROM equipment WHERE equipment_id = ?";
//
//        try (Connection conn = dbConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, equipmentId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return new Equipment(
//                            rs.getInt("equipment_id"),
//                            rs.getString("name"),
//                            rs.getString("model"),
//                            rs.getString("serial_number"),
//                            rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null,
//                            rs.getString("status"),
//                            rs.getString("category"),
//                            rs.getString("brand"),
//                            rs.getString("description")
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("Error fetching equipment by ID: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }
    public Equipment getEquipmentById(Connection conn, int equipmentId) {
        String query = "SELECT * FROM equipment WHERE equipment_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getInt("equipment_id"),
                            rs.getString("name"),
                            rs.getString("model"),
                            rs.getString("serial_number"),
                            rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null,
                            rs.getString("status"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("quantity"),
                            rs.getInt("issued_quantity"), // Include this
                            rs.getString("dsr_number")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


//    public boolean updateEquipmentStatus(int equipmentId, String status) {
//        String query = "UPDATE equipment SET status = ? WHERE equipment_id = ?";
//
//        try (Connection conn = dbConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, status);
//            pstmt.setInt(2, equipmentId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error updating equipment status: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }
    public boolean updateEquipmentStatus(Connection conn, int equipmentId, String status) throws SQLException{
        String query="UPDATE equipment SET status = ? WHERE equipment_id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, status);
            pstmt.setInt(2, equipmentId);
            return pstmt.executeUpdate()>0;
        }
    }

    public boolean isSerialNumberExists(String serialNumber, int excludeId) {
        String query = "SELECT COUNT(*) FROM equipment WHERE serial_number = ? AND equipment_id != ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, serialNumber);
            pstmt.setInt(2, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking serial number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDsrNumberExists(String dsrNumber, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Equipment WHERE dsr_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dsrNumber);
            pstmt.setInt(2, excludeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking DSR number existence: " + e.getMessage());
        }

        return false;
    }
}

