package com.college.inventoryerp.controller;

//package com.college.inventory.controller;

import com.college.inventoryerp.dao.EmployeeDAO;
import com.college.inventoryerp.model.Employee;
import com.college.inventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    // Table and columns
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> designationColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private TableColumn<Employee, String> phoneColumn;

    // Form controls
    @FXML private TextField nameField;
    @FXML private TextField designationField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    // Buttons
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    // Search
    @FXML private TextField searchField;

    private EmployeeDAO employeeDAO;
    private ObservableList<Employee> employeeList;
    private FilteredList<Employee> filteredEmployees;
    private Employee selectedEmployee;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeDAO = new EmployeeDAO();
        employeeList = FXCollections.observableArrayList();

        setupTable();
        setupFormControls();
        setupSearch();
        loadEmployeeData();

        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        designationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Table selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedEmployee = newSelection;
                    if (selectedEmployee != null) {
                        populateForm(selectedEmployee);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearForm();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                }
        );
    }

    private void setupFormControls() {
        // Add listeners for validation
        nameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText)) {
                ValidationUtils.highlightError(nameField);
            } else {
                ValidationUtils.clearError(nameField);
            }
        });

        designationField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText)) {
                ValidationUtils.highlightError(designationField);
            } else {
                ValidationUtils.clearError(designationField);
            }
        });

        emailField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText) || !ValidationUtils.isValidEmail(newText)) {
                ValidationUtils.highlightError(emailField);
            } else {
                ValidationUtils.clearError(emailField);
            }
        });

        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            if (ValidationUtils.isNotEmpty(newText) && !ValidationUtils.isValidPhone(newText)) {
                ValidationUtils.highlightError(phoneField);
            } else {
                ValidationUtils.clearError(phoneField);
            }
        });
    }

    private void setupSearch() {
        filteredEmployees = new FilteredList<>(employeeList, p -> true);
        employeeTable.setItems(filteredEmployees);

        // Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredEmployees.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return employee.getName().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getDesignation().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        (employee.getPhone() != null && employee.getPhone().contains(lowerCaseFilter));
            });
        });
    }

    private void loadEmployeeData() {
        try {
            employeeList.clear();
            employeeList.addAll(employeeDAO.getAllEmployees());
        } catch (Exception e) {
            ValidationUtils.showError("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Employee employee) {
        nameField.setText(employee.getName());
        designationField.setText(employee.getDesignation());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone());
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!ValidationUtils.validateTextField(nameField, "Employee Name")) {
            isValid = false;
        }

        if (!ValidationUtils.validateTextField(designationField, "Designation")) {
            isValid = false;
        }

        if (!ValidationUtils.validateEmail(emailField)) {
            isValid = false;
        }

        if (!ValidationUtils.validatePhone(phoneField)) {
            isValid = false;
        }

        // Check for duplicate email
        String email = emailField.getText();
        int excludeId = selectedEmployee != null ? selectedEmployee.getEmployeeId() : -1;
        if (employeeDAO.isEmailExists(email, excludeId)) {
            ValidationUtils.highlightError(emailField);
            ValidationUtils.showError("Email already exists. Please use a different email address.");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            return;
        }

        Employee employee = new Employee();
        employee.setName(nameField.getText().trim());
        employee.setDesignation(designationField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());

        if (employeeDAO.addEmployee(employee)) {
            ValidationUtils.showSuccess("Employee added successfully!");
            loadEmployeeData();
            clearForm();
        } else {
            ValidationUtils.showError("Failed to add employee. Please try again.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEmployee == null) {
            ValidationUtils.showError("Please select an employee to update.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        selectedEmployee.setName(nameField.getText().trim());
        selectedEmployee.setDesignation(designationField.getText().trim());
        selectedEmployee.setEmail(emailField.getText().trim());
        selectedEmployee.setPhone(phoneField.getText().trim());

        if (employeeDAO.updateEmployee(selectedEmployee)) {
            ValidationUtils.showSuccess("Employee updated successfully!");
            loadEmployeeData();
        } else {
            ValidationUtils.showError("Failed to update employee. Please try again.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEmployee == null) {
            ValidationUtils.showError("Please select an employee to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Employee");
        confirmAlert.setContentText("Are you sure you want to delete this employee?\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (employeeDAO.deleteEmployee(selectedEmployee.getEmployeeId())) {
                ValidationUtils.showSuccess("Employee deleted successfully!");
                loadEmployeeData();
                clearForm();
            } else {
                ValidationUtils.showError("Failed to delete employee. They may be referenced by issue records.");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        employeeTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadEmployeeData();
        ValidationUtils.showSuccess("Employee data refreshed!");
    }

    private void clearForm() {
        nameField.clear();
        designationField.clear();
        emailField.clear();
        phoneField.clear();

        // Clear error highlighting
        ValidationUtils.clearError(nameField);
        ValidationUtils.clearError(designationField);
        ValidationUtils.clearError(emailField);
        ValidationUtils.clearError(phoneField);
    }

    // Method to refresh data (can be called from other controllers)
    public void refresh() {
        loadEmployeeData();
    }
}
