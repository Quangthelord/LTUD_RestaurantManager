package com.restaurantmanagement.repository;

import com.restaurantmanagement.model.Shift;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for Shift entities.
 * Uses ArrayList for data storage.
 */
public class InMemoryShiftRepository {
    private final List<Shift> shifts;
    private int nextId = 1;

    public InMemoryShiftRepository() {
        this.shifts = new ArrayList<>();
    }

    /**
     * Save a new shift or update an existing one.
     */
    public Shift save(Shift shift) {
        if (shift.getId() == null || shift.getId().isEmpty()) {
            // New shift - assign ID
            shift.setId("SHF" + String.format("%04d", nextId++));
            shifts.add(shift);
            return shift;
        } else {
            // Update existing shift
            Optional<Shift> existing = findById(shift.getId());
            if (existing.isPresent()) {
                int index = shifts.indexOf(existing.get());
                shifts.set(index, shift);
                return shift;
            } else {
                shifts.add(shift);
                return shift;
            }
        }
    }

    /**
     * Find shift by ID.
     */
    public Optional<Shift> findById(String id) {
        return shifts.stream()
                .filter(shift -> shift.getId().equals(id))
                .findFirst();
    }

    /**
     * Get all shifts.
     */
    public List<Shift> findAll() {
        return new ArrayList<>(shifts);
    }

    /**
     * Delete shift by ID.
     */
    public boolean deleteById(String id) {
        return shifts.removeIf(shift -> shift.getId().equals(id));
    }

    /**
     * Check if shift exists by ID.
     */
    public boolean existsById(String id) {
        return shifts.stream().anyMatch(shift -> shift.getId().equals(id));
    }

    /**
     * Find shifts by employee ID.
     */
    public List<Shift> findByEmployeeId(String employeeId) {
        return shifts.stream()
                .filter(shift -> shift.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    /**
     * Find shifts by date.
     */
    public List<Shift> findByDate(LocalDate date) {
        return shifts.stream()
                .filter(shift -> shift.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Find shifts by date range.
     */
    public List<Shift> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return shifts.stream()
                .filter(shift -> !shift.getDate().isBefore(startDate) && !shift.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
}




















