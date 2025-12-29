package com.restaurantmanagement.service;

import com.restaurantmanagement.model.Employee;
import com.restaurantmanagement.repository.InMemoryEmployeeRepository;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Employee business logic.
 */
public class EmployeeService {
    private final InMemoryEmployeeRepository repository;

    public EmployeeService() {
        this.repository = new InMemoryEmployeeRepository();
    }

    /**
     * Add a new employee.
     */
    public Employee addEmployee(Employee employee) {
        validateEmployee(employee);
        return repository.save(employee);
    }

    /**
     * Update an existing employee.
     */
    public Employee updateEmployee(Employee employee) {
        if (employee.getId() == null || employee.getId().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required for update");
        }
        if (!repository.existsById(employee.getId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getId() + " not found");
        }
        validateEmployee(employee);
        return repository.save(employee);
    }

    /**
     * Delete an employee by ID.
     */
    public boolean deleteEmployee(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty");
        }
        return repository.deleteById(id);
    }

    /**
     * Get employee by ID.
     */
    public Optional<Employee> getEmployeeById(String id) {
        return repository.findById(id);
    }

    /**
     * Get all employees.
     */
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    /**
     * Search employees by name.
     */
    public List<Employee> searchEmployeesByName(String name) {
        return repository.findByName(name);
    }

    /**
     * Validate employee data.
     */
    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee position is required");
        }
    }
}

