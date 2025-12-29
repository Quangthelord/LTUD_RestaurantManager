package com.restaurantmanagement.controller;

import com.restaurantmanagement.model.Employee;
import com.restaurantmanagement.model.Shift;
import com.restaurantmanagement.service.EmployeeService;
import com.restaurantmanagement.service.ShiftService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Shift management UI.
 * Handles user interactions and updates the view.
 */
public class ShiftController {
    private final ShiftService shiftService;
    private final EmployeeService employeeService;
    private final ObservableList<Shift> shiftList;
    private TableView<Shift> tableView;

    // UI Components (for form inputs)
    private TextField idField;
    private ComboBox<String> employeeComboBox;
    private DatePicker datePicker;
    private ComboBox<Integer> startHourComboBox;
    private ComboBox<Integer> startMinuteComboBox;
    private ComboBox<Integer> endHourComboBox;
    private ComboBox<Integer> endMinuteComboBox;
    private ComboBox<String> shiftTypeComboBox;

    public ShiftController() {
        this.shiftService = new ShiftService();
        this.employeeService = new EmployeeService();
        this.shiftList = FXCollections.observableArrayList();
        loadShifts();
    }
    
    /**
     * Constructor with shared EmployeeService instance.
     */
    public ShiftController(com.restaurantmanagement.service.EmployeeService employeeService) {
        this.shiftService = new ShiftService();
        this.employeeService = employeeService;
        this.shiftList = FXCollections.observableArrayList();
        loadShifts();
    }

    /**
     * Set the TableView reference.
     */
    public void setTableView(TableView<Shift> tableView) {
        this.tableView = tableView;
        tableView.setItems(shiftList);
    }

    /**
     * Set form input fields.
     */
    public void setFormFields(TextField idField, ComboBox<String> employeeComboBox,
                             DatePicker datePicker, ComboBox<Integer> startHourComboBox,
                             ComboBox<Integer> startMinuteComboBox, ComboBox<Integer> endHourComboBox,
                             ComboBox<Integer> endMinuteComboBox, ComboBox<String> shiftTypeComboBox) {
        this.idField = idField;
        this.employeeComboBox = employeeComboBox;
        this.datePicker = datePicker;
        this.startHourComboBox = startHourComboBox;
        this.startMinuteComboBox = startMinuteComboBox;
        this.endHourComboBox = endHourComboBox;
        this.endMinuteComboBox = endMinuteComboBox;
        this.shiftTypeComboBox = shiftTypeComboBox;
        
        // Populate employee combo box
        populateEmployeeComboBox();
        
        // Populate hour combo boxes (0-23)
        for (int i = 0; i < 24; i++) {
            startHourComboBox.getItems().add(i);
            endHourComboBox.getItems().add(i);
        }
        
        // Populate minute combo boxes (0, 15, 30, 45)
        for (int i = 0; i < 60; i += 15) {
            startMinuteComboBox.getItems().add(i);
            endMinuteComboBox.getItems().add(i);
        }
        
        // Set default values
        startHourComboBox.setValue(9);
        startMinuteComboBox.setValue(0);
        endHourComboBox.setValue(17);
        endMinuteComboBox.setValue(0);
        
        // Populate shift type combo box
        shiftTypeComboBox.getItems().addAll("Morning", "Afternoon", "Evening", "Night");
    }

    /**
     * Populate employee combo box with available employees.
     */
    private void populateEmployeeComboBox() {
        if (employeeComboBox == null) {
            return;
        }
        List<Employee> employees = employeeService.getAllEmployees();
        employeeComboBox.getItems().clear();
        for (Employee emp : employees) {
            employeeComboBox.getItems().add(emp.getId() + " - " + emp.getName());
        }
    }
    
    /**
     * Public method to refresh employee combo box.
     * Can be called when employees are added/updated/deleted.
     */
    public void refreshEmployeeComboBox() {
        populateEmployeeComboBox();
    }

    /**
     * Load all shifts into the table.
     */
    public void loadShifts() {
        shiftList.clear();
        shiftList.addAll(shiftService.getAllShifts());
    }

    /**
     * Handle add button click.
     */
    public void handleAdd() {
        try {
            Shift shift = createShiftFromForm();
            shiftService.addShift(shift);
            loadShifts();
            clearForm();
            populateEmployeeComboBox(); // Refresh employee list
            showSuccessAlert("Shift assigned successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error assigning shift", e.getMessage());
        }
    }

    /**
     * Handle update button click.
     */
    public void handleUpdate() {
        try {
            Shift shift = createShiftFromForm();
            if (shift.getId() == null || shift.getId().isEmpty()) {
                showErrorAlert("Error", "Please select a shift to update");
                return;
            }
            shiftService.updateShift(shift);
            loadShifts();
            clearForm();
            populateEmployeeComboBox(); // Refresh employee list
            showSuccessAlert("Shift updated successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error updating shift", e.getMessage());
        }
    }

    /**
     * Handle delete button click.
     */
    public void handleDelete() {
        Shift selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Error", "Please select a shift to delete");
            return;
        }

        try {
            shiftService.deleteShift(selected.getId());
            loadShifts();
            clearForm();
            showSuccessAlert("Shift deleted successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error deleting shift", e.getMessage());
        }
    }

    /**
     * Handle table row selection.
     */
    public void handleTableSelection() {
        Shift selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        }
    }

    /**
     * Create Shift object from form fields.
     */
    private Shift createShiftFromForm() {
        Shift shift = new Shift();
        shift.setId(idField.getText().trim());
        
        // Parse employee selection
        String employeeSelection = employeeComboBox.getValue();
        if (employeeSelection != null && !employeeSelection.isEmpty()) {
            String employeeId = employeeSelection.split(" - ")[0];
            String employeeName = employeeSelection.contains(" - ") ? 
                employeeSelection.substring(employeeSelection.indexOf(" - ") + 3) : "";
            
            // Get employee name from service
            Employee emp = employeeService.getEmployeeById(employeeId).orElse(null);
            if (emp != null) {
                shift.setEmployeeId(emp.getId());
                shift.setEmployeeName(emp.getName());
            } else {
                shift.setEmployeeId(employeeId);
                shift.setEmployeeName(employeeName);
            }
        }
        
        shift.setDate(datePicker.getValue());
        
        // Create LocalTime from hour and minute combo boxes
        Integer startHour = startHourComboBox.getValue();
        Integer startMinute = startMinuteComboBox.getValue();
        if (startHour != null && startMinute != null) {
            shift.setStartTime(LocalTime.of(startHour, startMinute));
        }
        
        Integer endHour = endHourComboBox.getValue();
        Integer endMinute = endMinuteComboBox.getValue();
        if (endHour != null && endMinute != null) {
            shift.setEndTime(LocalTime.of(endHour, endMinute));
        }
        
        shift.setShiftType(shiftTypeComboBox.getValue());
        
        return shift;
    }

    /**
     * Get shifts for a specific date.
     */
    public List<Shift> getShiftsByDate(LocalDate date) {
        return shiftList.stream()
                .filter(shift -> shift.getDate() != null && shift.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Populate form fields with selected shift data.
     */
    private void populateForm(Shift shift) {
        idField.setText(shift.getId());
        
        // Set employee in combo box
        String employeeDisplay = shift.getEmployeeId() + " - " + shift.getEmployeeName();
        employeeComboBox.setValue(employeeDisplay);
        
        datePicker.setValue(shift.getDate());
        
        // Set hour and minute combo boxes
        if (shift.getStartTime() != null) {
            startHourComboBox.setValue(shift.getStartTime().getHour());
            startMinuteComboBox.setValue(shift.getStartTime().getMinute());
        }
        
        if (shift.getEndTime() != null) {
            endHourComboBox.setValue(shift.getEndTime().getHour());
            endMinuteComboBox.setValue(shift.getEndTime().getMinute());
        }
        
        shiftTypeComboBox.setValue(shift.getShiftType());
    }

    /**
     * Clear form fields.
     */
    private void clearForm() {
        idField.clear();
        employeeComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        startHourComboBox.setValue(9);
        startMinuteComboBox.setValue(0);
        endHourComboBox.setValue(17);
        endMinuteComboBox.setValue(0);
        shiftTypeComboBox.setValue(null);
        if (tableView != null) {
            tableView.getSelectionModel().clearSelection();
        }
    }

    /**
     * Show success alert.
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get observable list for binding.
     */
    public ObservableList<Shift> getShiftList() {
        return shiftList;
    }
}




















