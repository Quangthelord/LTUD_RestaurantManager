package com.restaurantmanagement.service;

import com.restaurantmanagement.model.Shift;
import com.restaurantmanagement.repository.InMemoryShiftRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Shift business logic.
 */
public class ShiftService {
    private final InMemoryShiftRepository repository;

    public ShiftService() {
        this.repository = new InMemoryShiftRepository();
    }

    /**
     * Add a new shift.
     */
    public Shift addShift(Shift shift) {
        validateShift(shift);
        return repository.save(shift);
    }

    /**
     * Update an existing shift.
     */
    public Shift updateShift(Shift shift) {
        if (shift.getId() == null || shift.getId().isEmpty()) {
            throw new IllegalArgumentException("Shift ID is required for update");
        }
        if (!repository.existsById(shift.getId())) {
            throw new IllegalArgumentException("Shift with ID " + shift.getId() + " not found");
        }
        validateShift(shift);
        return repository.save(shift);
    }

    /**
     * Delete a shift by ID.
     */
    public boolean deleteShift(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Shift ID cannot be empty");
        }
        return repository.deleteById(id);
    }

    /**
     * Get shift by ID.
     */
    public Optional<Shift> getShiftById(String id) {
        return repository.findById(id);
    }

    /**
     * Get all shifts.
     */
    public List<Shift> getAllShifts() {
        return repository.findAll();
    }

    /**
     * Get shifts by employee ID.
     */
    public List<Shift> getShiftsByEmployeeId(String employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    /**
     * Get shifts by date.
     */
    public List<Shift> getShiftsByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    /**
     * Get shifts by date range.
     */
    public List<Shift> getShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate);
    }

    /**
     * Validate shift data.
     */
    private void validateShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        if (shift.getEmployeeId() == null || shift.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (shift.getDate() == null) {
            throw new IllegalArgumentException("Shift date is required");
        }
        if (shift.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (shift.getEndTime() == null) {
            throw new IllegalArgumentException("End time is required");
        }
        if (shift.getStartTime().isAfter(shift.getEndTime()) || shift.getStartTime().equals(shift.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (shift.getShiftType() == null || shift.getShiftType().trim().isEmpty()) {
            throw new IllegalArgumentException("Shift type is required");
        }
    }
}




















