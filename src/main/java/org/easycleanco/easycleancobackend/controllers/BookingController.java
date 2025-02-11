package org.easycleanco.easycleancobackend.controllers;

import org.easycleanco.easycleancobackend.models.Booking;
import org.easycleanco.easycleancobackend.models.BookingDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @PostMapping()
    public ResponseEntity<String> createBooking(@RequestBody ArrayList<Booking> bookings) {
        BookingDAO bookingDAO = new BookingDAO();
        int affectedRows = 0;
        try {
            for (Booking booking : bookings) {
                affectedRows += bookingDAO.createBooking(booking);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating booking");
        }

        if (affectedRows == 0) {
            return ResponseEntity.badRequest().body("Error creating booking");
        }

        return ResponseEntity.ok("Booking created");
    }

    @GetMapping()
    public ResponseEntity<ArrayList<Booking>> getAllBookings(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceName
    ) {
        BookingDAO bookingDAO = new BookingDAO();
        ArrayList<Booking> bookings = new ArrayList<>();
        try {
            bookings = bookingDAO.getAllBookings(startDate, endDate, status, serviceName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(bookings);
    }

    @PutMapping("{bookingId}/status")
    public ResponseEntity<String> updateBookingStatus(@PathVariable int bookingId, @RequestBody String status) {
        BookingDAO bookingDAO = new BookingDAO();
        int affectedRows = 0;
        try {
            affectedRows = bookingDAO.updateBookingStatus(bookingId, status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error updating booking status");
        }

        if (affectedRows == 0) {
            return ResponseEntity.badRequest().body("Error updating booking status");
        }

        return ResponseEntity.ok("Booking status updated");
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/daily")
    public ResponseEntity<Map<String, Integer>> getDailyBookings() {
        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Integer> dailyBookings = new HashMap<>();
        try {
            dailyBookings = bookingDAO.getBookingsByDay();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(dailyBookings);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Integer>> getWeeklyBookings() {
        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Integer> weeklyBookings = new HashMap<>();
        try {
            weeklyBookings = bookingDAO.getBookingsByWeek();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(weeklyBookings);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Integer>> getMonthlyBookings() {
        BookingDAO bookingDAO = new BookingDAO();
        Map<String, Integer> monthlyBookings = new HashMap<>();
        try {
            monthlyBookings = bookingDAO.getBookingsByMonth();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(monthlyBookings);
    }


}
