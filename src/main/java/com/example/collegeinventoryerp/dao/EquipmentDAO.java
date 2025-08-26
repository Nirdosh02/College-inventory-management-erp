package com.example.collegeinventoryerp.dao;

//package com.college.inventory.dao;

import com.example.collegeinventoryerp.model.Equipment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

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
                        rs.getString("brand"),
                        rs.getString("description")
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
        String query = "SELECT * FROM equipment WHERE status = 'available' ORDER BY name";

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
                        rs.getString("brand"),
                        rs.getString("description")
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
        String query = "INSERT INTO equipment (name, model, serial_number, purchase_date, status, category, brand, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getModel());
            pstmt.setString(3, equipment.getSerialNumber());
            pstmt.setDate(4, equipment.getPurchaseDate() != null ? Date.valueOf(equipment.getPurchaseDate()) : null);
            pstmt.setString(5, equipment.getStatus());
            pstmt.setString(6, equipment.getCategory());
            pstmt.setString(7, equipment.getBrand());
            pstmt.setString(8, equipment.getDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEquipment(Equipment equipment) {
        String query = "UPDATE equipment SET name = ?, model = ?, serial_number = ?, purchase_date = ?, status = ?, category = ?, brand = ?, description = ? WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getModel());
            pstmt.setString(3, equipment.getSerialNumber());
            pstmt.setDate(4, equipment.getPurchaseDate() != null ? Date.valueOf(equipment.getPurchaseDate()) : null);
            pstmt.setString(5, equipment.getStatus());
            pstmt.setString(6, equipment.getCategory());
            pstmt.setString(7, equipment.getBrand());
            pstmt.setString(8, equipment.getDescription());
            pstmt.setInt(9, equipment.getEquipmentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating equipment: " + e.getMessage());
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
        public Equipment getEquipmentById(Connection conn, int equipmentId) throws SQLException {
            String query = "SELECT * FROM equipment WHERE equipment_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, equipmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Equipment eq = new Equipment();
                        eq.setEquipmentId(rs.getInt("equipment_id"));
                        eq.setName(rs.getString("name"));
                        eq.setStatus(rs.getString("status"));
                        return eq;
                    }
                }
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
}

