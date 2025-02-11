package org.easycleanco.easycleancobackend.controllers;

import org.easycleanco.easycleancobackend.models.BookingDAO;
import org.easycleanco.easycleancobackend.models.SalesDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sales")
public class SalesController {
    @GetMapping("top-10-customers")
    public ResponseEntity<Map<String, Float>> getTop10Customer() {
        SalesDAO salesDAO = new SalesDAO();
        Map<String, Float> top10Customers = new HashMap<>();
        try {
            top10Customers = salesDAO.getTop10CustomersWithHighestSpending();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(top10Customers);
    }
}
