package com.example.collegeinventoryerp.controller;

//package com.college.inventory.controller;

import com.example.collegeinventoryerp.dao.*;
import com.example.collegeinventoryerp.model.*;
import com.example.collegeinventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TabPane mainTabPane;
    @FXML private Label dashboardTitle;
    @FXML private Label totalEquipmentLabel;
    @FXML private Label availableEquipmentLabel;
    @FXML private Label issuedEquipmentLabel;
    @FXML private Label totalFacultyLabel;
//    @FXML private Label totalEmployeesLabel;

    // Issue/Return Tab
    @FXML private ComboBox<Equipment> equipmentComboBox;
    @FXML private ComboBox<Faculty> facultyComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private DatePicker issueDatePicker;
    @FXML private TextArea notesTextArea;
    @FXML private Button issueButton;

    @FXML private TableView<IssueRecord> currentIssuesTable;
    @FXML private TableColumn<IssueRecord, String> currentEquipmentColumn;
    @FXML private TableColumn<IssueRecord, String> currentFacultyColumn;
    @FXML private TableColumn<IssueRecord, String> currentEmployeeColumn;
    @FXML private TableColumn<IssueRecord, LocalDate> currentIssueDateColumn;
    @FXML private Button returnButton;

    // DAO instances
    private EquipmentDAO equipmentDAO;
    private FacultyDAO facultyDAO;
    private EmployeeDAO employeeDAO;
    private IssueRecordDAO issueRecordDAO;

    // Data lists
    private ObservableList<Equipment> availableEquipment;
    private ObservableList<Faculty> facultyList;
    private ObservableList<Employee> employeeList;
    private ObservableList<IssueRecord> currentIssues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAOs
        equipmentDAO = new EquipmentDAO();
        facultyDAO = new FacultyDAO();
        employeeDAO = new EmployeeDAO();
        issueRecordDAO = new IssueRecordDAO();

        // Initialize data lists
        availableEquipment = FXCollections.observableArrayList();
        facultyList = FXCollections.observableArrayList();
        employeeList = FXCollections.observableArrayList();
        currentIssues = FXCollections.observableArrayList();

        // Setup UI components
        setupIssueReturnTab();
        setupCurrentIssuesTable();

        // Load initial data
        loadDashboardData();
        refreshData();

        // Set default issue date to today
        issueDatePicker.setValue(LocalDate.now());
    }

    private void setupIssueReturnTab() {
        equipmentComboBox.setItems(availableEquipment);
        facultyComboBox.setItems(facultyList);
        employeeComboBox.setItems(employeeList);

        // Setup combo box display formats
        equipmentComboBox.setCellFactory(lv -> new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });

        equipmentComboBox.setButtonCell(new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });

        facultyComboBox.setCellFactory(lv -> new ListCell<Faculty>() {
            @Override
            protected void updateItem(Faculty item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName() + " (" + item.getDepartment() + ")");
            }
        });

        facultyComboBox.setButtonCell(new ListCell<Faculty>() {
            @Override
            protected void updateItem(Faculty item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName() + " (" + item.getDepartment() + ")");
            }
        });
    }

    private void setupCurrentIssuesTable() {
        currentEquipmentColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentNameProperty());
        currentFacultyColumn.setCellValueFactory(cellData -> cellData.getValue().facultyNameProperty());
        currentEmployeeColumn.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        currentIssueDateColumn.setCellValueFactory(cellData -> cellData.getValue().issueDateProperty());

        currentIssuesTable.setItems(currentIssues);

        // Enable return button only when a record is selected
        returnButton.disableProperty().bind(
                currentIssuesTable.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    private void loadDashboardData() {
        try {
            ObservableList<Equipment> allEquipment = equipmentDAO.getAllEquipment();

            long totalEquipment = allEquipment.size();
            long availableCount = allEquipment.stream().filter(eq -> "available".equals(eq.getStatus())).count();
            long issuedCount = allEquipment.stream().filter(eq -> "issued".equals(eq.getStatus())).count();

            totalEquipmentLabel.setText(String.valueOf(totalEquipment));
            availableEquipmentLabel.setText(String.valueOf(availableCount));
            issuedEquipmentLabel.setText(String.valueOf(issuedCount));

            totalFacultyLabel.setText(String.valueOf(facultyDAO.getAllFaculty().size()));
//            totalEmployeesLabel.setText(String.valueOf(employeeDAO.getAllEmployees().size()));

        } catch (Exception e) {
            ValidationUtils.showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshData() {
        try {
            // Refresh available equipment
            availableEquipment.clear();
            availableEquipment.addAll(equipmentDAO.getAvailableEquipment());

            // Refresh faculty list
            facultyList.clear();
            facultyList.addAll(facultyDAO.getAllFaculty());

            // Refresh employee list
            employeeList.clear();
            employeeList.addAll(employeeDAO.getAllEmployees());

            // Refresh current issues
            currentIssues.clear();
            currentIssues.addAll(issueRecordDAO.getCurrentlyIssuedRecords());

            // Refresh dashboard data
            loadDashboardData();

        } catch (Exception e) {
            ValidationUtils.showError("Error refreshing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIssueEquipment() {
        // Validate selections
        Equipment selectedEquipment = equipmentComboBox.getSelectionModel().getSelectedItem();
        Faculty selectedFaculty = facultyComboBox.getSelectionModel().getSelectedItem();
        Employee selectedEmployee = employeeComboBox.getSelectionModel().getSelectedItem();
        LocalDate issueDate = issueDatePicker.getValue();

        if (selectedEquipment == null) {
            ValidationUtils.showError("Please select an equipment to issue.");
            return;
        }

        if (selectedFaculty == null) {
            ValidationUtils.showError("Please select a faculty member.");
            return;
        }

        if (selectedEmployee == null) {
            ValidationUtils.showError("Please select an employee.");
            return;
        }

        if (issueDate == null) {
            ValidationUtils.showError("Please select an issue date.");
            return;
        }

        // Check if equipment is already issued
        if (issueRecordDAO.isEquipmentCurrentlyIssued(selectedEquipment.getEquipmentId())) {
            ValidationUtils.showError("This equipment is already issued to someone.");
            return;
        }

        // Issue the equipment
        boolean success = issueRecordDAO.issueEquipment(
                selectedEquipment.getEquipmentId(),
                selectedFaculty.getFacultyId(),
                selectedEmployee.getEmployeeId(),
                issueDate,
                notesTextArea.getText()
        );

        if (success) {
            ValidationUtils.showSuccess("Equipment issued successfully!");
            clearIssueForm();
            refreshData();
        } else {
            ValidationUtils.showError("Failed to issue equipment. Please try again.");
        }
    }

    @FXML
    private void handleReturnEquipment() {
        IssueRecord selectedRecord = currentIssuesTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            ValidationUtils.showError("Please select a record to return.");
            return;
        }

        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Return");
        confirmDialog.setHeaderText("Return Equipment");
        confirmDialog.setContentText("Are you sure you want to return this equipment?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = issueRecordDAO.returnEquipment(
                    selectedRecord.getRecordId(),
                    LocalDate.now()
            );

            if (success) {
                ValidationUtils.showSuccess("Equipment returned successfully!");
                refreshData();
            } else {
                ValidationUtils.showError("Failed to return equipment. Please try again.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        refreshData();
        ValidationUtils.showSuccess("Data refreshed successfully!");
    }

    private void clearIssueForm() {
        equipmentComboBox.getSelectionModel().clearSelection();
        facultyComboBox.getSelectionModel().clearSelection();
        employeeComboBox.getSelectionModel().clearSelection();
        issueDatePicker.setValue(LocalDate.now());
        notesTextArea.clear();
    }

    // Public method to refresh data when called from other controllers
    public void refresh() {
        refreshData();
    }
}

