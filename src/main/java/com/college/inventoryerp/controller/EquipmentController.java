package com.example.collegeinventoryerp.controller;

//package com.college.inventory.controller;

import com.example.collegeinventoryerp.dao.EquipmentDAO;
import com.example.collegeinventoryerp.model.Equipment;
import com.example.collegeinventoryerp.utils.ReportGenerator;
import com.example.collegeinventoryerp.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.example.collegeinventoryerp.utils.ValidationUtils.showError;

public class EquipmentController implements Initializable {

    // Table and columns
    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TableColumn<Equipment, Integer> idColumn;
    @FXML private TableColumn<Equipment, String> nameColumn;
    @FXML private TableColumn<Equipment, String> modelColumn;
    @FXML private TableColumn<Equipment, String> serialColumn;
    @FXML private TableColumn<Equipment, LocalDate> purchaseDateColumn;
    @FXML private TableColumn<Equipment, String> statusColumn;
    @FXML private TableColumn<Equipment, String> categoryColumn;
    @FXML private  TableColumn<Equipment, Integer> quantityColumn;
    @FXML private TableColumn<Equipment, String> dsrNumberColumn;

    // Form controls
    @FXML private TextField nameField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private DatePicker purchaseDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField categoryField;
    @FXML private Spinner<Integer> quantityField;
    @FXML private TextField dsrNumberField;
    @FXML private TextArea descriptionArea;

    // Buttons
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    // Search and filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;

    private EquipmentDAO equipmentDAO;
    private ObservableList<Equipment> equipmentList;
    private FilteredList<Equipment> filteredEquipment;
    private Equipment selectedEquipment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equipmentDAO = new EquipmentDAO();
        equipmentList = FXCollections.observableArrayList();

        setupTable();
        setupFormControls();
        setupSearchAndFilter();
        loadEquipmentData();

        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dsrNumberColumn.setCellValueFactory(new PropertyValueFactory<>("dsrNumber"));

        // Style status column based on status
        statusColumn.setCellFactory(column -> new TableCell<Equipment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "available":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                            break;
                        case "issued":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                            break;
                        case "maintenance":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
                            break;
                        case "not available":
                            setStyle("-fx-background-color: #e2e3e5; -fx-text-fill: #383d41;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Table selection listener
        equipmentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedEquipment = newSelection;
                    if (selectedEquipment != null) {
                        populateForm(selectedEquipment);
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
        // Setup status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "available", "issued", "maintenance", "not available"
        ));
        statusComboBox.setValue("available");

        if (quantityField != null) {
            quantityField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        }

        // Add listeners to form fields for validation
        nameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText)) {
                ValidationUtils.highlightError(nameField);
            } else {
                ValidationUtils.clearError(nameField);
            }
        });

        serialNumberField.textProperty().addListener((obs, oldText, newText) -> {
            if (!ValidationUtils.isNotEmpty(newText)) {
                ValidationUtils.highlightError(serialNumberField);
            } else {
                ValidationUtils.clearError(serialNumberField);
            }
        });
    }

    private void setupSearchAndFilter() {
        filteredEquipment = new FilteredList<>(equipmentList, p -> true);
        equipmentTable.setItems(filteredEquipment);

        // Setup status filter
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "available", "issued", "maintenance", "not available"
        ));
        statusFilterComboBox.setValue("All");

        // Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        statusFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
    }

    private void updateFilters() {
        filteredEquipment.setPredicate(equipment -> {
            // Status filter
            String statusFilter = statusFilterComboBox.getValue();
            if (statusFilter != null && !statusFilter.equals("All")) {
                if (!equipment.getStatus().equalsIgnoreCase(statusFilter)) {
                    return false;
                }
            }

            // Search filter
            String searchText = searchField.getText();
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();
            return equipment.getName().toLowerCase().contains(lowerCaseFilter) ||
                    equipment.getModel().toLowerCase().contains(lowerCaseFilter) ||
                    equipment.getSerialNumber().toLowerCase().contains(lowerCaseFilter) ||
                    equipment.getCategory().toLowerCase().contains(lowerCaseFilter);
//                    equipment.getBrand().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void loadEquipmentData() {
        try {
            equipmentList.clear();
            equipmentList.addAll(equipmentDAO.getAllEquipment());
        } catch (Exception e) {
            showError("Error loading equipment data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Equipment equipment) {
        nameField.setText(equipment.getName());
        modelField.setText(equipment.getModel());
        serialNumberField.setText(equipment.getSerialNumber());
        purchaseDatePicker.setValue(equipment.getPurchaseDate());
        statusComboBox.setValue(equipment.getStatus());
        categoryField.setText(equipment.getCategory());
        if (quantityField != null) quantityField.getValueFactory().setValue(equipment.getQuantity());
        dsrNumberField.setText(equipment.getDsrNumber());

        descriptionArea.setText(equipment.getDescription());
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!ValidationUtils.validateTextField(nameField, "Equipment Name")) {
            isValid = false;
        }

        if (!ValidationUtils.validateTextField(serialNumberField, "Serial Number")) {
            isValid = false;
        }

        // Check for duplicate serial numbers
        String serialNumber = serialNumberField.getText();
        int excludeId = selectedEquipment != null ? selectedEquipment.getEquipmentId() : -1;
        if (equipmentDAO.isSerialNumberExists(serialNumber, excludeId)) {
            ValidationUtils.highlightError(serialNumberField);
            showError("Serial number already exists. Please use a different serial number.");
            isValid = false;
        }
        if (selectedEquipment == null) {
            if (equipmentDAO.isDsrNumberExists(dsrNumberField.getText(), 0)) {
                showError("DSR number already exists. Please use a different DSR number.");
                return false;
            }
        }

        return isValid;
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            return;
        }

        Equipment equipment = new Equipment();
        equipment.setName(nameField.getText());
        equipment.setModel(modelField.getText());
        equipment.setSerialNumber(serialNumberField.getText());
        equipment.setPurchaseDate(purchaseDatePicker.getValue());
        equipment.setStatus(statusComboBox.getValue());
        equipment.setCategory(categoryField.getText());
        equipment.setQuantity(quantityField.getValueFactory().getValue());
        equipment.setDsrNumber(dsrNumberField.getText());
        equipment.setDescription(descriptionArea.getText());

        if (equipmentDAO.addEquipment(equipment)) {
            ValidationUtils.showSuccess("Equipment added successfully!");
            loadEquipmentData();
            clearForm();
        } else {
            showError("Failed to add equipment. Please try again.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEquipment == null) {
            showError("Please select an equipment to update.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        selectedEquipment.setName(nameField.getText());
        selectedEquipment.setModel(modelField.getText());
        selectedEquipment.setSerialNumber(serialNumberField.getText());
        selectedEquipment.setPurchaseDate(purchaseDatePicker.getValue());
        selectedEquipment.setStatus(statusComboBox.getValue());
        selectedEquipment.setCategory(categoryField.getText());
        selectedEquipment.setQuantity(quantityField.getValueFactory().getValue());
        selectedEquipment.setDsrNumber(dsrNumberField.getText());
        selectedEquipment.setDescription(descriptionArea.getText());

        if (equipmentDAO.updateEquipment(selectedEquipment)) {
            ValidationUtils.showSuccess("Equipment updated successfully!");
            loadEquipmentData();
        } else {
            showError("Failed to update equipment. Please try again.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEquipment == null) {
            showError("Please select an equipment to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Equipment");
        confirmAlert.setContentText("Are you sure you want to delete this equipment?\nThis action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (equipmentDAO.deleteEquipment(selectedEquipment.getEquipmentId())) {
                ValidationUtils.showSuccess("Equipment deleted successfully!");
                loadEquipmentData();
                clearForm();
            } else {
                showError("Failed to delete equipment. It may be currently issued or referenced by other records.");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        equipmentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadEquipmentData();
        ValidationUtils.showSuccess("Equipment data refreshed!");
    }

    @FXML
    private void handleExportExcel() {
        ObservableList<Equipment> dataToExport = filteredEquipment.isEmpty() ? equipmentList :
                FXCollections.observableArrayList(filteredEquipment);
        ReportGenerator.exportEquipmentToExcel(dataToExport, equipmentTable.getScene().getWindow());
    }

    @FXML
    private void handleExportCSV() {
        ObservableList<Equipment> dataToExport = filteredEquipment.isEmpty() ? equipmentList :
                FXCollections.observableArrayList(filteredEquipment);
        ReportGenerator.exportToCSV(dataToExport, equipmentTable.getScene().getWindow());
    }

    private void clearForm() {
        nameField.clear();
        modelField.clear();
        serialNumberField.clear();
        purchaseDatePicker.setValue(null);
        statusComboBox.setValue("available");
        categoryField.clear();
//        brandField.clear();
        descriptionArea.clear();

        // Clear error highlighting
        ValidationUtils.clearError(nameField);
        ValidationUtils.clearError(serialNumberField);
    }

    // Method to refresh data (can be called from other controllers)
    public void refresh() {
        loadEquipmentData();
    }
}

