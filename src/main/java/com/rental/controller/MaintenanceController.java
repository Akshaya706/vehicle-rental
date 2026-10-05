package com.rental.controller;

import com.rental.dto.MaintenanceRequest;
import com.rental.entity.Maintenance;
import com.rental.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleMaintenance(@RequestBody MaintenanceRequest request) {
        try {
            Maintenance maintenance = maintenanceService.scheduleMaintenance(request);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Maintenance updated = maintenanceService.updateMaintenanceStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenance());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Maintenance>> getVehicleMaintenance(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(maintenanceService.getVehicleMaintenance(vehicleId));
    }

    @GetMapping("/expiry-alerts")
    public ResponseEntity<List<Map<String, Object>>> getExpiryAlerts() {
        return ResponseEntity.ok(maintenanceService.getExpiryNotifications());
    }
}
