package com.example.collegeinventoryerp.controller;

//package com.college.inventory.controller;

import com.example.collegeinventoryerp.dao.FacultyDAO;
import com.example.collegeinventoryerp.model.Faculty;
import com.example.collegeinventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FacultyController implements Initializable {

    // Table and columns
    @FXML private TableView<Faculty> facultyTable;
    @FXML private TableColumn<Faculty, Integer> idColumn;
    @FXML private TableColumn<Faculty, String> nameColumn;
    @FXML private TableColumn<Faculty, String> departmentColumn;
    @FXML private TableColumn<Faculty, String> emailColumn;
    @FXML private TableColumn<Faculty, String> phoneColumn;

    // Form controls
    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    // Buttons
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    // Search and filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilterComboBox;

    private FacultyDAO facultyDAO;
    private ObservableList<Faculty> facultyList;
    private FilteredList<Faculty> filteredFaculty;
    private Faculty selectedFaculty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        facultyDAO = new FacultyDAO();
        facultyList = FXCollections.observableArrayList();

        setupTable();
        setupFormControls();
        setupSearchAndFilter();
        loadFacultyData();

        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("facultyId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Table selection listener
        facultyTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedFaculty = newSelection;
                    if (selectedFaculty != null) {
                        populateForm(selectedFaculty);
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

        departmentField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText)) {
                ValidationUtils.highlightError(departmentField);
            } else {
                ValidationUtils.clearError(departmentField);
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

    private void setupSearchAndFilter() {
        filteredFaculty = new FilteredList<>(facultyList, p -> true);
        facultyTable.setItems(filteredFaculty);

        // Setup department filter with common departments
        departmentFilterComboBox.setItems(FXCollections.observableArrayList(
                "All Departments", "Computer Science", "Electronics", "Mechanical",
                "Information Technology", "Electrical", "Civil", "Chemical"
        ));
        departmentFilterComboBox.setValue("All Departments");

        // Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        departmentFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
    }

    private void updateFilters() {
        filteredFaculty.setPredicate(faculty -> {
            // Department filter
            String departmentFilter = departmentFilterComboBox.getValue();
            if (departmentFilter != null && !departmentFilter.equals("All Departments")) {
                if (!faculty.getDepartment().equalsIgnoreCase(departmentFilter)) {
                    return false;
                }
            }

            // Search filter
            String searchText = searchField.getText();
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();
            return faculty.getName().toLowerCase().contains(lowerCaseFilter) ||
                    faculty.getDepartment().toLowerCase().contains(lowerCaseFilter) ||
                    faculty.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                    (faculty.getPhone() != null && faculty.getPhone().contains(lowerCaseFilter));
        });
    }

    private void loadFacultyData() {
        try {
            facultyList.clear();
            facultyList.addAll(facultyDAO.getAllFaculty());
            updateDepartmentFilter();
        } catch (Exception e) {
            ValidationUtils.showError("Error loading faculty data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateDepartmentFilter() {
        // Update department filter with actual departments from data
        ObservableList<String> departments = FXCollections.observableArrayList("All Departments");
        facultyList.stream()
                .map(Faculty::getDepartment)
                .distinct()
                .sorted()
                .forEach(departments::add);

        String currentSelection = departmentFilterComboBox.getValue();
        departmentFilterComboBox.setItems(departments);
        if (departments.contains(currentSelection)) {
            departmentFilterComboBox.setValue(currentSelection);
        } else {
            departmentFilterComboBox.setValue("All Departments");
        }
    }

    private void populateForm(Faculty faculty) {
        nameField.setText(faculty.getName());
        departmentField.setText(faculty.getDepartment());
        emailField.setText(faculty.getEmail());
        phoneField.setText(faculty.getPhone());
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!ValidationUtils.validateTextField(nameField, "Faculty Name")) {
            isValid = false;
        }

        if (!ValidationUtils.validateTextField(departmentField, "Department")) {
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
        int excludeId = selectedFaculty != null ? selectedFaculty.getFacultyId() : -1;
        if (facultyDAO.isEmailExists(email, excludeId)) {
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

        Faculty faculty = new Faculty();
        faculty.setName(nameField.getText().trim());
        faculty.setDepartment(departmentField.getText().trim());
        faculty.setEmail(emailField.getText().trim());
        faculty.setPhone(phoneField.getText().trim());

        if (facultyDAO.addFaculty(faculty)) {
            ValidationUtils.showSuccess("Faculty added successfully!");
            loadFacultyData();
            clearForm();
        } else {
            ValidationUtils.showError("Failed to add faculty. Please try again.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedFaculty == null) {
            ValidationUtils.showError("Please select a faculty member to update.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        selectedFaculty.setName(nameField.getText().trim());
        selectedFaculty.setDepartment(departmentField.getText().trim());
        selectedFaculty.setEmail(emailField.getText().trim());
        selectedFaculty.setPhone(phoneField.getText().trim());

        if (facultyDAO.updateFaculty(selectedFaculty)) {
            ValidationUtils.showSuccess("Faculty updated successfully!");
            loadFacultyData();
        } else {
            ValidationUtils.showError("Failed to update faculty. Please try again.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedFaculty == null) {
            ValidationUtils.showError("Please select a faculty member to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Faculty");
        confirmAlert.setContentText("Are you sure you want to delete this faculty member?\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (facultyDAO.deleteFaculty(selectedFaculty.getFacultyId())) {
                ValidationUtils.showSuccess("Faculty deleted successfully!");
                loadFacultyData();
                clearForm();
            } else {
                ValidationUtils.showError("Failed to delete faculty. They may be referenced by issue records.");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        facultyTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadFacultyData();
        ValidationUtils.showSuccess("Faculty data refreshed!");
    }

    private void clearForm() {
        nameField.clear();
        departmentField.clear();
        emailField.clear();
        phoneField.clear();

        // Clear error highlighting
        ValidationUtils.clearError(nameField);
        ValidationUtils.clearError(departmentField);
        ValidationUtils.clearError(emailField);
        ValidationUtils.clearError(phoneField);
    }

    // Method to refresh data (can be called from other controllers)
    public void refresh() {
        loadFacultyData();
    }
}
