package com.restaurantmanagement.repository;

import com.restaurantmanagement.model.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for Employee entities.
 * Uses ArrayList for data storage.
 */
public class InMemoryEmployeeRepository {
    private final List<Employee> employees;
    private int nextId = 1;

    public InMemoryEmployeeRepository() {
        this.employees = new ArrayList<>();
    }

    /**
     * Save a new employee or update an existing one.
     */
    public Employee save(Employee employee) {
        if (employee.getId() == null || employee.getId().isEmpty()) {
            // New employee - assign ID
            employee.setId("EMP" + String.format("%04d", nextId++));
            employees.add(employee);
            return employee;
        } else {
            // Update existing employee
            Optional<Employee> existing = findById(employee.getId());
            if (existing.isPresent()) {
                int index = employees.indexOf(existing.get());
                employees.set(index, employee);
                return employee;
            } else {
                employees.add(employee);
                return employee;
            }
        }
    }

    /**
     * Find employee by ID.
     */
    public Optional<Employee> findById(String id) {
        return employees.stream()
                .filter(emp -> emp.getId().equals(id))
                .findFirst();
    }

    /**
     * Get all employees.
     */
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    /**
     * Delete employee by ID.
     */
    public boolean deleteById(String id) {
        return employees.removeIf(emp -> emp.getId().equals(id));
    }

    /**
     * Check if employee exists by ID.
     */
    public boolean existsById(String id) {
        return employees.stream().anyMatch(emp -> emp.getId().equals(id));
    }

    /**
     * Find employees by name (case-insensitive partial match).
     */
    public List<Employee> findByName(String name) {
        return employees.stream()
                .filter(emp -> emp.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}

