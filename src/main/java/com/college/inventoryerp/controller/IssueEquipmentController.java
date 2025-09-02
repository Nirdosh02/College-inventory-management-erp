package com.college.inventoryerp.controller;

import com.college.inventoryerp.dao.EmployeeDAO;
import com.college.inventoryerp.dao.EquipmentDAO;
import com.college.inventoryerp.dao.FacultyDAO;
import com.college.inventoryerp.dao.IssueRecordDAO;
import com.college.inventoryerp.model.Employee;
import com.college.inventoryerp.model.Equipment;
import com.college.inventoryerp.model.Faculty;
import com.college.inventoryerp.model.IssueRecord;
import com.college.inventoryerp.utils.EmailService;
import com.college.inventoryerp.utils.ValidationUtils;
import javafx.scene.control.SpinnerValueFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class IssueEquipmentController implements Initializable {

    @FXML private ComboBox<Equipment> equipmentComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private ComboBox<Faculty> facultyComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private Spinner<Integer> issueQuantityField;
    @FXML private DatePicker issueDatePicker;
    @FXML private TextArea notesTextArea;
    @FXML private CheckBox chkSendEmail;
    @FXML private Button issueButton;

    @FXML private TableView<IssueRecord> currentIssuesTable;
    @FXML private TableColumn<IssueRecord, String> currentEquipmentColumn;
    @FXML private TableColumn<IssueRecord, String> currentFacultyColumn;
    @FXML private TableColumn<IssueRecord, String> currentEmployeeColumn;
    @FXML private TableColumn<IssueRecord, LocalDate> currentIssueDateColumn;
    @FXML private TableColumn<IssueRecord, Integer> currentQuantityColumn;
    @FXML private TableColumn<IssueRecord, Integer> currentAvailableQuantityColumn;
    @FXML private Button returnButton;

    // DAOs
    private EquipmentDAO equipmentDAO;
    private FacultyDAO facultyDAO;
    private EmployeeDAO employeeDAO;
    private IssueRecordDAO issueRecordDAO;

    // Data lists
    private ObservableList<Equipment> availableEquipment;
    private ObservableList<String> departmentList;
    private ObservableList<Faculty> facultyList;
    private ObservableList<Faculty> allFacultyList;
    private ObservableList<Employee> employeeList;
    private ObservableList<IssueRecord> currentIssues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equipmentDAO = new EquipmentDAO();
        facultyDAO = new FacultyDAO();
        employeeDAO = new EmployeeDAO();
        issueRecordDAO = new IssueRecordDAO();

        availableEquipment = FXCollections.observableArrayList();
        departmentList = FXCollections.observableArrayList();
        facultyList = FXCollections.observableArrayList();
        allFacultyList=FXCollections.observableArrayList();
        employeeList = FXCollections.observableArrayList();
        currentIssues = FXCollections.observableArrayList();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        issueQuantityField.setValueFactory(valueFactory);

        setupIssueReturnTab();
        setupCurrentIssuesTable();
        setupDepartmentFilter();

        refreshData();

        issueDatePicker.setValue(LocalDate.now());
    }

    private void setupIssueReturnTab() {
        equipmentComboBox.setItems(availableEquipment);
        departmentComboBox.setItems(departmentList);
        facultyComboBox.setItems(facultyList);
        employeeComboBox.setItems(employeeList);

        equipmentComboBox.setCellFactory(lv -> new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });

        equipmentComboBox.setButtonCell(new ListCell<Equipment>() {
            @Override
            protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getSerialNumber() + ")");
            }
        });

        facultyComboBox.setCellFactory(lv -> new ListCell<Faculty>() {
            @Override
            protected void updateItem(Faculty item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getDepartment() + ")");
            }
        });

        facultyComboBox.setButtonCell(new ListCell<Faculty>() {
            @Override
            protected void updateItem(Faculty item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getDepartment() + ")");
            }
        });
    }

    private void setupDepartmentFilter(){
        departmentComboBox.setOnAction(event -> {
            String selectedDepartment = departmentComboBox.getSelectionModel().getSelectedItem();
            filterFacultyByDepartment(selectedDepartment);
        });
        departmentComboBox.setPromptText("Select Department");
        facultyComboBox.setPromptText("Select Faculty");
    }

    private void filterFacultyByDepartment(String selectedDepartment) {
        facultyList.clear();
        facultyComboBox.getSelectionModel().clearSelection();
        if(selectedDepartment==null || selectedDepartment.isEmpty()){
            facultyList.addAll(allFacultyList);
        } else{
            ObservableList<Faculty> filteredFaculty = facultyDAO.getFacultyByDepartment(selectedDepartment);
            facultyList.addAll(filteredFaculty);
        }
    }

    private void setupCurrentIssuesTable() {
        currentEquipmentColumn.setCellValueFactory(cellData -> cellData.getValue().equipmentNameProperty());
        currentFacultyColumn.setCellValueFactory(cellData -> cellData.getValue().facultyNameProperty());
        currentEmployeeColumn.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        currentIssueDateColumn.setCellValueFactory(cellData -> cellData.getValue().issueDateProperty());
//        currentQuantityColumn.setCellValueFactory(cellData->  new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        currentQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        currentAvailableQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().availableQuantityProperty().asObject());

        currentIssuesTable.setItems(currentIssues);

        returnButton.disableProperty().bind(
                currentIssuesTable.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    private void refreshData() {
        try {
            availableEquipment.clear();
            availableEquipment.addAll(equipmentDAO.getAvailableEquipment());

            departmentList.clear();
            departmentList.addAll("All departments");
            departmentList.addAll(facultyDAO.getAllDepartments());

            facultyList.clear();
            facultyList.addAll(facultyDAO.getAllFaculty());

            employeeList.clear();
            employeeList.addAll(employeeDAO.getAllEmployees());

            currentIssues.clear();
            currentIssues.addAll(issueRecordDAO.getCurrentlyIssuedRecords());

        } catch (Exception e) {
            ValidationUtils.showError("Error refreshing issue/return data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIssueEquipment() {
        Equipment selectedEquipment = equipmentComboBox.getSelectionModel().getSelectedItem();
        Faculty selectedFaculty = facultyComboBox.getSelectionModel().getSelectedItem();
        Employee selectedEmployee = employeeComboBox.getSelectionModel().getSelectedItem();
        LocalDate issueDate = issueDatePicker.getValue();

        if (selectedEquipment == null) {
            ValidationUtils.showError("Please select an equipment to issue.");
            return;
        }
        if(departmentComboBox.getSelectionModel().getSelectedItem() == null){
            ValidationUtils.showError("Please select a department first.");
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
        int requestedQty;
        try {
//            requestedQty = Integer.parseInt(String.valueOf(issueQuantityField.getValueFactory().getValue()));
            requestedQty = issueQuantityField.getValue();
        } catch (NumberFormatException e) {
            ValidationUtils.showError("Please enter a valid quantity.");
            return;
        }
        // Check available stock
        int available = selectedEquipment.getQuantity() - selectedEquipment.getIssuedQuantity();
        System.out.println("DEBUG Controller: Total=" + selectedEquipment.getQuantity() +
                ", Issued=" + selectedEquipment.getIssuedQuantity() +
                ", Available=" + available +
                ", Requested=" + requestedQty);
        if (requestedQty <= 0) {
            ValidationUtils.showError("Quantity must be greater than 0.");
            return;
        }
        if (requestedQty > available) {
            ValidationUtils.showError("Not enough stock. Available: " + available);
            return;
        }

        if (issueDate == null) {
            ValidationUtils.showError("Please select an issue date.");
            return;
        }
//        if (issueRecordDAO.isEquipmentCurrentlyIssued(selectedEquipment.getEquipmentId())) {
//            ValidationUtils.showError("This equipment is already issued to someone.");
//            return;
//        }
        IssueRecord issueRecord = new IssueRecord(
                0, // recordId will be set by DB if auto-increment
                selectedEquipment.getEquipmentId(),
                selectedFaculty.getFacultyId(),
                selectedEmployee.getEmployeeId(),
                issueDate,
                null,  // returnDate
                "issued",
                notesTextArea.getText(),
                new SimpleIntegerProperty(requestedQty),
                selectedEquipment.getQuantity() - (selectedEquipment.getIssuedQuantity() + requestedQty), // available after issue
                selectedEquipment.getName(),
                selectedFaculty.getName(),
                selectedEmployee.getName()
        );


        boolean success = issueRecordDAO.issueEquipment(
                selectedEquipment.getEquipmentId(),
                selectedFaculty.getFacultyId(),
                selectedEmployee.getEmployeeId(),
                issueDate,
                notesTextArea.getText(),
                requestedQty   // pass quantity
        );

        if (success) {
            ValidationUtils.showSuccess("Equipment issued successfully!");
            if(chkSendEmail.isSelected()){
                EmailService.sendIssueConfirmationEmail(issueRecord, selectedFaculty.getEmail());
            }
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

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Return");
        confirmDialog.setHeaderText("Return Equipment");
        confirmDialog.setContentText("Are you sure you want to return this equipment?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // ✅ Step 1: get the equipmentId and quantity issued
//            int equipmentId = selectedRecord.getEquipmentId();
//            int issuedQty = selectedRecord.getQuantityIssued(); // make sure IssueRecord has quantity field

            // ✅ Step 2: update stock in Equipment table
//            boolean stockUpdated = equipmentDAO.issueEquipment(equipmentId, issuedQty);

            // ✅ Step 3: update issue record (mark as returned)
            boolean recordUpdated = issueRecordDAO.returnEquipment(
                    selectedRecord.getRecordId(),
                    LocalDate.now(),
                    selectedRecord.getEquipmentId(),
                    selectedRecord.getQuantity()
            );

            if (recordUpdated) {
                ValidationUtils.showSuccess("Equipment returned successfully!");
                refreshData();
            } else {
                ValidationUtils.showError("Failed to return equipment. Please try again.");
            }
        }
    }

    //    private void handleReturnEquipment() {
//        IssueRecord selectedRecord = currentIssuesTable.getSelectionModel().getSelectedItem();
//        if (selectedRecord == null) {
//            ValidationUtils.showError("Please select a record to return.");
//            return;
//        }
//
//        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmDialog.setTitle("Confirm Return");
//        confirmDialog.setHeaderText("Return Equipment");
//        confirmDialog.setContentText("Are you sure you want to return this equipment?");
//
//        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
//            boolean success = issueRecordDAO.returnEquipment(
//                    selectedRecord.getRecordId(),
//                    LocalDate.now()
//            );
//
//            if (success) {
//                ValidationUtils.showSuccess("Equipment returned successfully!");
//                refreshData();
//            } else {
//                ValidationUtils.showError("Failed to return equipment. Please try again.");
//            }
//        }
//    }
//
    @FXML
    private void handleRefresh() {
        refreshData();
        ValidationUtils.showSuccess("Data refreshed successfully!");
    }

    private void clearIssueForm() {
        equipmentComboBox.getSelectionModel().clearSelection();
        departmentComboBox.getSelectionModel().clearSelection();
        facultyComboBox.getSelectionModel().clearSelection();
        employeeComboBox.getSelectionModel().clearSelection();
        issueDatePicker.setValue(LocalDate.now());
        notesTextArea.clear();

        facultyList.clear();
        facultyList.addAll(allFacultyList);
    }
}
