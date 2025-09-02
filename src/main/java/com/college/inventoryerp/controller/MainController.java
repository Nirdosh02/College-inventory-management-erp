package com.college.inventoryerp.controller;

import com.college.inventoryerp.dao.EmployeeDAO;
import com.college.inventoryerp.dao.EquipmentDAO;
import com.college.inventoryerp.dao.FacultyDAO;
import com.college.inventoryerp.dao.IssueRecordDAO;
import com.college.inventoryerp.model.Employee;
import com.college.inventoryerp.model.Equipment;
import com.college.inventoryerp.model.Faculty;
import com.college.inventoryerp.model.IssueRecord;
import com.college.inventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML private TabPane mainTabPane;
    @FXML private Label dashboardTitle;
    @FXML private ComboBox<Equipment> equipmentFilterCombo;
    @FXML private Label totalEquipmentSummary;
    @FXML private Label availableEquipmentSummary;
    @FXML private Label issuedEquipmentSummary;
    @FXML private Label maintenanceEquipmentSummary;
//    @FXML private Label totalFacultySummary;
//    @FXML private Label totalEmployeeSummary;
//    @FXML private Label totalIssuesSummary;
//    @FXML private Label pendingReturnsSummary;
    private ObservableList<Equipment> equipmentList;
    @FXML private Button refreshEquipmentButton;

    // DAOs
    private EquipmentDAO equipmentDAO;
    private FacultyDAO facultyDAO;
    private EmployeeDAO employeeDAO;
    private IssueRecordDAO issueRecordDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equipmentDAO = new EquipmentDAO();
        facultyDAO = new FacultyDAO();
        employeeDAO = new EmployeeDAO();
        issueRecordDAO = new IssueRecordDAO();

        equipmentList = FXCollections.observableArrayList(equipmentDAO.getAllEquipment());
        equipmentFilterCombo.setItems(equipmentList);
        equipmentFilterCombo.setCellFactory(lv -> new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });
        equipmentFilterCombo.setButtonCell(new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });
// Listen for selection changes
        equipmentFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateEquipmentSummary(newVal));

        loadDashboardData();
    }

    private void loadDashboardData() {
        try {

            ObservableList<Equipment> allEquipment = equipmentDAO.getAllEquipment();
            ObservableList<Faculty> allFaculty = facultyDAO.getAllFaculty();
            ObservableList<Employee> allEmployees = employeeDAO.getAllEmployees();
            ObservableList<IssueRecord> allRecords = issueRecordDAO.getAllIssueRecords();
//            long totalEquipment = (allEquipment == null) ? 0 : allEquipment.size();
//            long availableCount = (allEquipment == null) ? 0 : allEquipment.stream()
//                    .filter(eq -> "available".equalsIgnoreCase(eq.getStatus())).count();
//            long issuedCount = (allEquipment == null) ? 0 : allEquipment.stream()
//                    .filter(eq -> "issued".equalsIgnoreCase(eq.getStatus())).count();
//            long totalEquipment = allEquipment.size();
//            long availableCount = allEquipment.stream().filter(eq -> "available".equals(eq.getStatus())).count();
//            long issuedCount = allEquipment.stream().filter(eq -> "issued".equals(eq.getStatus())).count();
//            long maintenanceCount = allEquipment.stream().filter(eq -> "maintenance".equals(eq.getStatus())).count();
//
//            totalEquipmentSummary.setText(String.valueOf(totalEquipment));
//            availableEquipmentSummary.setText(String.valueOf(availableCount));
//            issuedEquipmentSummary.setText(String.valueOf(issuedCount));
//            maintenanceEquipmentSummary.setText(String.valueOf(maintenanceCount));

            // People summary
//            totalFacultySummary.setText(String.valueOf(allFaculty.size()));
//            totalEmployeeSummary.setText(String.valueOf(allEmployees.size()));

            // Issue records summary
//            totalIssuesSummary.setText(String.valueOf(allRecords.size()));
//            long pendingReturns = allRecords.stream().filter(record -> "issued".equals(record.getStatus())).count();
//            pendingReturnsSummary.setText(String.valueOf(pendingReturns));

        } catch (Exception e) {
            ValidationUtils.showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void customGetData(){

    }

//    public void setupFilter(){
//
//        equipmentFilterCombo.setItems(equipmentList);
//        equipmentFilterCombo.setCellFactory(lv -> new ListCell<Equipment>() {
//            @Override
//            protected void updateItem(Equipment item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
//            }
//        });
//
//        equipmentFilterCombo.setButtonCell(new ListCell<Equipment>() {
//            @Override
//            protected void updateItem(Equipment item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
//            }
//        });
//    }

//    @FXML
//    private void handleRefresh() {
//        loadDashboardData();
//        ValidationUtils.showSuccess("Dashboard refreshed successfully!");
//    }

    @FXML
    private void handleRefreshEquipment() {
        // Reload data from DAO
        equipmentList.setAll(equipmentDAO.getAllEquipment());
        Equipment selected = equipmentFilterCombo.getSelectionModel().getSelectedItem();
        updateEquipmentSummary(selected);
        ValidationUtils.showSuccess("Equipment summary refreshed!");
    }

    // Updates summary cards based on selected equipment
    private void updateEquipmentSummary(Equipment equipment) {
        if (equipment == null) {
            // Optionally show all counts, or blank
            loadDashboardData();
            return;
        }
        totalEquipmentSummary.setText(String.valueOf(equipment.getQuantity()));

        // Assuming you have appropriate methods/fields for these counts:
        availableEquipmentSummary.setText(String.valueOf(equipment.getAvailableQuantity()));
        issuedEquipmentSummary.setText(String.valueOf(equipment.getIssuedQuantity()));
        maintenanceEquipmentSummary.setText(String.valueOf(equipment.getMaintenance()));
    }


    // Allow other controllers to refresh dashboard if needed
    public void refresh() {
        equipmentList.clear();
        equipmentList.addAll(equipmentDAO.getAllEquipment());
        loadDashboardData();
    }
}
