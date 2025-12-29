package com.restaurantmanagement.controller;

import com.restaurantmanagement.model.Employee;
import com.restaurantmanagement.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller for Employee management UI.
 * Handles user interactions and updates the view.
 */
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ObservableList<Employee> employeeList;
    private TableView<Employee> tableView;

    // UI Components (for form inputs)
    private TextField idField;
    private TextField nameField;
    private TextField positionField;
    private TextField phoneField;
    private TextField emailField;

    public EmployeeController() {
        this.employeeService = new EmployeeService();
        this.employeeList = FXCollections.observableArrayList();
        loadEmployees();
    }
    
    /**
     * Constructor with shared EmployeeService instance.
     */
    public EmployeeController(com.restaurantmanagement.service.EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.employeeList = FXCollections.observableArrayList();
        loadEmployees();
    }

    /**
     * Set the TableView reference.
     */
    public void setTableView(TableView<Employee> tableView) {
        this.tableView = tableView;
        tableView.setItems(employeeList);
    }

    /**
     * Set form input fields.
     */
    public void setFormFields(TextField idField, TextField nameField, TextField positionField,
                              TextField phoneField, TextField emailField) {
        this.idField = idField;
        this.nameField = nameField;
        this.positionField = positionField;
        this.phoneField = phoneField;
        this.emailField = emailField;
    }

    /**
     * Load all employees into the table.
     */
    public void loadEmployees() {
        employeeList.clear();
        employeeList.addAll(employeeService.getAllEmployees());
    }

    /**
     * Handle add button click.
     */
    public void handleAdd() {
        try {
            Employee employee = createEmployeeFromForm();
            employeeService.addEmployee(employee);
            loadEmployees();
            clearForm();
            showSuccessAlert("Employee added successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error adding employee", e.getMessage());
        }
    }

    /**
     * Handle update button click.
     */
    public void handleUpdate() {
        try {
            Employee employee = createEmployeeFromForm();
            if (employee.getId() == null || employee.getId().isEmpty()) {
                showErrorAlert("Error", "Please select an employee to update");
                return;
            }
            employeeService.updateEmployee(employee);
            loadEmployees();
            clearForm();
            showSuccessAlert("Employee updated successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error updating employee", e.getMessage());
        }
    }

    /**
     * Handle delete button click.
     */
    public void handleDelete() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Error", "Please select an employee to delete");
            return;
        }

        try {
            employeeService.deleteEmployee(selected.getId());
            loadEmployees();
            clearForm();
            showSuccessAlert("Employee deleted successfully!");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Error deleting employee", e.getMessage());
        }
    }

    /**
     * Handle table row selection.
     */
    public void handleTableSelection() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        }
    }

    /**
     * Create Employee object from form fields.
     */
    private Employee createEmployeeFromForm() {
        Employee employee = new Employee();
        employee.setId(idField.getText().trim());
        employee.setName(nameField.getText().trim());
        employee.setPosition(positionField.getText().trim());
        employee.setPhoneNumber(phoneField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        return employee;
    }

    /**
     * Populate form fields with selected employee data.
     */
    private void populateForm(Employee employee) {
        idField.setText(employee.getId());
        nameField.setText(employee.getName());
        positionField.setText(employee.getPosition());
        phoneField.setText(employee.getPhoneNumber());
        emailField.setText(employee.getEmail());
    }

    /**
     * Clear form fields.
     */
    private void clearForm() {
        idField.clear();
        nameField.clear();
        positionField.clear();
        phoneField.clear();
        emailField.clear();
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
    public ObservableList<Employee> getEmployeeList() {
        return employeeList;
    }
}

