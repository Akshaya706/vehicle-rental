package com.rental.controller;

import com.rental.dto.VehicleReturnRequest;
import com.rental.entity.DamageReport;
import com.rental.service.DamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/damage")
@CrossOrigin(origins = "*")
public class DamageController {

    @Autowired
    private DamageService damageService;

    @PostMapping("/return")
    public ResponseEntity<?> processReturn(@RequestBody VehicleReturnRequest request) {
        try {
            Map<String, Object> res = damageService.processVehicleReturn(request);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<List<DamageReport>> getAllDamageReports() {
        return ResponseEntity.ok(damageService.getAllDamageReports());
    }
}
