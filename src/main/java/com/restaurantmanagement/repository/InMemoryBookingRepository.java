package com.restaurantmanagement.repository;

import com.restaurantmanagement.model.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for Booking entities.
 */
public class InMemoryBookingRepository {
    private final List<Booking> bookings;
    private int nextId = 1;

    public InMemoryBookingRepository() {
        this.bookings = new ArrayList<>();
    }

    /**
     * Save a new booking or update an existing one.
     */
    public Booking save(Booking booking) {
        if (booking.getId() == null || booking.getId().isEmpty()) {
            // New booking - assign ID
            booking.setId("BK" + String.format("%04d", nextId++));
            bookings.add(booking);
            return booking;
        } else {
            // Update existing booking
            Optional<Booking> existing = findById(booking.getId());
            if (existing.isPresent()) {
                int index = bookings.indexOf(existing.get());
                bookings.set(index, booking);
                return booking;
            } else {
                bookings.add(booking);
                return booking;
            }
        }
    }

    /**
     * Find booking by ID.
     */
    public Optional<Booking> findById(String id) {
        return bookings.stream()
                .filter(booking -> booking.getId().equals(id))
                .findFirst();
    }

    /**
     * Get all bookings.
     */
    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }

    /**
     * Delete booking by ID.
     */
    public boolean deleteById(String id) {
        return bookings.removeIf(booking -> booking.getId().equals(id));
    }

    /**
     * Check if booking exists by ID.
     */
    public boolean existsById(String id) {
        return bookings.stream().anyMatch(booking -> booking.getId().equals(id));
    }

    /**
     * Find bookings by date.
     */
    public List<Booking> findByDate(java.time.LocalDate date) {
        return bookings.stream()
                .filter(booking -> booking.getDate() != null && booking.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Find bookings by status.
     */
    public List<Booking> findByStatus(String status) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * Find bookings by customer name (case-insensitive partial match).
     */
    public List<Booking> findByCustomerName(String name) {
        String searchName = name.toLowerCase();
        return bookings.stream()
                .filter(booking -> booking.getCustomerName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }
}



















