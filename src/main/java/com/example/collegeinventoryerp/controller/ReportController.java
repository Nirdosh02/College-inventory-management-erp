package com.example.collegeinventoryerp.controller;

//package com.college.inventory.controller;

import com.example.collegeinventoryerp.dao.*;
import com.example.collegeinventoryerp.model.*;
import com.example.collegeinventoryerp.utils.ReportGenerator;
import com.example.collegeinventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    // Equipment Reports
    @FXML private TableView<Equipment> availableEquipmentTable;
    @FXML private TableColumn<Equipment, String> availableNameColumn;
    @FXML private TableColumn<Equipment, String> availableModelColumn;
    @FXML private TableColumn<Equipment, String> availableSerialColumn;
    @FXML private TableColumn<Equipment, String> availableCategoryColumn;
    @FXML private TableColumn<Equipment, String> availableBrandColumn;

    // Issue Records Reports
    @FXML private TableView<IssueRecord> issuedEquipmentTable;
    @FXML private TableColumn<IssueRecord, String> issuedEquipmentColumn;
    @FXML private TableColumn<IssueRecord, String> issuedFacultyColumn;
    @FXML private TableColumn<IssueRecord, String> issuedEmployeeColumn;
    @FXML private TableColumn<IssueRecord, LocalDate> issuedDateColumn;
    @FXML private TableColumn<IssueRecord, String> issuedNotesColumn;

    // All Issue Records
    @FXML private TableView<IssueRecord> allRecordsTable;
    @FXML private TableColumn<IssueRecord, Integer> allRecordIdColumn;
    @FXML private TableColumn<IssueRecord, String> allEquipmentColumn;
    @FXML private TableColumn<IssueRecord, String> allFacultyColumn;
    @FXML private TableColumn<IssueRecord, String> allEmployeeColumn;
    @FXML private TableColumn<IssueRecord, LocalDate> allIssueDateColumn;
    @FXML private TableColumn<IssueRecord, LocalDate> allReturnDateColumn;
    @FXML private TableColumn<IssueRecord, String> allStatusColumn;

    // Summary Labels
    @FXML private Label totalEquipmentSummary;
    @FXML private Label availableEquipmentSummary;
    @FXML private Label issuedEquipmentSummary;
    @FXML private Label maintenanceEquipmentSummary;
    @FXML private Label totalFacultySummary;
    @FXML private Label totalEmployeeSummary;
    @FXML private Label totalIssuesSummary;
    @FXML private Label pendingReturnsSummary;

    // Filter controls
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    private EquipmentDAO equipmentDAO;
    private FacultyDAO facultyDAO;
    private EmployeeDAO employeeDAO;
    private IssueRecordDAO issueRecordDAO;

    private ObservableList<Equipment> availableEquipmentList;
    private ObservableList<IssueRecord> issuedRecordsList;
    private ObservableList<IssueRecord> allRecordsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAOs
        equipmentDAO = new EquipmentDAO();
        facultyDAO = new FacultyDAO();
        employeeDAO = new EmployeeDAO();
        issueRecordDAO = new IssueRecordDAO();

        // Initialize lists
        availableEquipmentList = FXCollections.observableArrayList();
        issuedRecordsList = FXCollections.observableArrayList();
        allRecordsList = FXCollections.observableArrayList();

        // Setup tables
        setupAvailableEquipmentTable();
        setupIssuedEquipmentTable();
        setupAllRecordsTable();
        setupFilters();

        // Load initial data
        loadReportData();
    }

    private void setupAvailableEquipmentTable() {
        availableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        availableModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        availableSerialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        availableCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availableBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        availableEquipmentTable.setItems(availableEquipmentList);
    }

    private void setupIssuedEquipmentTable() {
        issuedEquipmentColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        issuedFacultyColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        issuedEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        issuedDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        issuedNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        issuedEquipmentTable.setItems(issuedRecordsList);
    }

    private void setupAllRecordsTable() {
        allRecordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        allEquipmentColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        allFacultyColumn.setCellValueFactory(new PropertyValueFactory<>("facultyName"));
        allEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        allIssueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        allReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        allStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Style status column
        allStatusColumn.setCellFactory(column -> new TableCell<IssueRecord, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("issued".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    } else if ("returned".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        allRecordsTable.setItems(allRecordsList);
    }

    private void setupFilters() {
        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "available", "issued", "maintenance", "retired"
        ));
        statusFilterCombo.setValue("All");

        // Category filter
        categoryFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Computer", "Display", "Mobile", "Printer", "Network", "Scanner", "Photography"
        ));
        categoryFilterCombo.setValue("All");

        // Date range - default to current month
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
    }

    private void loadReportData() {
        try {
            // Load available equipment
            availableEquipmentList.clear();
            availableEquipmentList.addAll(equipmentDAO.getAvailableEquipment());

            // Load currently issued records
            issuedRecordsList.clear();
            issuedRecordsList.addAll(issueRecordDAO.getCurrentlyIssuedRecords());

            // Load all issue records
            allRecordsList.clear();
            allRecordsList.addAll(issueRecordDAO.getAllIssueRecords());

            // Update summary
            updateSummary();

        } catch (Exception e) {
            ValidationUtils.showError("Error loading report data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateSummary() {
        try {
            ObservableList<Equipment> allEquipment = equipmentDAO.getAllEquipment();
            ObservableList<Faculty> allFaculty = facultyDAO.getAllFaculty();
            ObservableList<Employee> allEmployees = employeeDAO.getAllEmployees();
            ObservableList<IssueRecord> allRecords = issueRecordDAO.getAllIssueRecords();

            // Equipment summary
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
            ValidationUtils.showError("Error updating summary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefreshReports() {
        loadReportData();
        ValidationUtils.showSuccess("Reports refreshed successfully!");
    }

    @FXML
    private void handleExportAvailableEquipment() {
        if (availableEquipmentList.isEmpty()) {
            ValidationUtils.showWarning("No available equipment data to export.");
            return;
        }
        ReportGenerator.exportEquipmentToExcel(availableEquipmentList, availableEquipmentTable.getScene().getWindow());
    }

    @FXML
    private void handleExportIssuedEquipment() {
        if (issuedRecordsList.isEmpty()) {
            ValidationUtils.showWarning("No issued equipment data to export.");
            return;
        }
        ReportGenerator.exportIssueRecordsToExcel(issuedRecordsList, issuedEquipmentTable.getScene().getWindow());
    }

    @FXML
    private void handleExportAllRecords() {
        if (allRecordsList.isEmpty()) {
            ValidationUtils.showWarning("No issue records data to export.");
            return;
        }
        ReportGenerator.exportIssueRecordsToExcel(allRecordsList, allRecordsTable.getScene().getWindow());
    }

    @FXML
    private void handleGenerateCustomReport() {
        // Implement custom report generation based on filters
        String statusFilter = statusFilterCombo.getValue();
        String categoryFilter = categoryFilterCombo.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        try {
            ObservableList<Equipment> filteredEquipment = equipmentDAO.getAllEquipment();

            // Apply filters
            if (statusFilter != null && !statusFilter.equals("All")) {
                filteredEquipment = filteredEquipment.filtered(eq -> eq.getStatus().equals(statusFilter));
            }

            if (categoryFilter != null && !categoryFilter.equals("All")) {
                filteredEquipment = filteredEquipment.filtered(eq -> eq.getCategory().equals(categoryFilter));
            }

            if (filteredEquipment.isEmpty()) {
                ValidationUtils.showWarning("No data matches the selected filters.");
                return;
            }

            // Export filtered data
            ReportGenerator.exportEquipmentToExcel(filteredEquipment, statusFilterCombo.getScene().getWindow());

        } catch (Exception e) {
            ValidationUtils.showError("Error generating custom report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to refresh data (can be called from other controllers)
    public void refresh() {
        loadReportData();
    }
}

