package com.example.collegeinventoryerp.controller;

import com.example.collegeinventoryerp.dao.EmployeeDAO;
import com.example.collegeinventoryerp.dao.EquipmentDAO;
import com.example.collegeinventoryerp.dao.FacultyDAO;
import com.example.collegeinventoryerp.dao.IssueRecordDAO;
import com.example.collegeinventoryerp.model.Employee;
import com.example.collegeinventoryerp.model.Equipment;
import com.example.collegeinventoryerp.model.Faculty;
import com.example.collegeinventoryerp.model.IssueRecord;
import com.example.collegeinventoryerp.utils.ValidationUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private Label dashboardTitle;
    @FXML private Label totalEquipmentSummary;
    @FXML private Label availableEquipmentSummary;
    @FXML private Label issuedEquipmentSummary;
    @FXML private Label maintenanceEquipmentSummary;
    @FXML private Label totalFacultySummary;
    @FXML private Label totalEmployeeSummary;
    @FXML private Label totalIssuesSummary;
    @FXML private Label pendingReturnsSummary;

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
            long totalEquipment = allEquipment.size();
            long availableCount = allEquipment.stream().filter(eq -> "available".equals(eq.getStatus())).count();
            long issuedCount = allEquipment.stream().filter(eq -> "issued".equals(eq.getStatus())).count();
            long maintenanceCount = allEquipment.stream().filter(eq -> "maintenance".equals(eq.getStatus())).count();

            totalEquipmentSummary.setText(String.valueOf(totalEquipment));
            availableEquipmentSummary.setText(String.valueOf(availableCount));
            issuedEquipmentSummary.setText(String.valueOf(issuedCount));
            maintenanceEquipmentSummary.setText(String.valueOf(maintenanceCount));

            // People summary
            totalFacultySummary.setText(String.valueOf(allFaculty.size()));
            totalEmployeeSummary.setText(String.valueOf(allEmployees.size()));

            // Issue records summary
            totalIssuesSummary.setText(String.valueOf(allRecords.size()));
            long pendingReturns = allRecords.stream().filter(record -> "issued".equals(record.getStatus())).count();
            pendingReturnsSummary.setText(String.valueOf(pendingReturns));

        } catch (Exception e) {
            ValidationUtils.showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadDashboardData();
        ValidationUtils.showSuccess("Dashboard refreshed successfully!");
    }

    // Allow other controllers to refresh dashboard if needed
    public void refresh() {
        loadDashboardData();
    }
}
