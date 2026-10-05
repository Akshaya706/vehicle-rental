package com.rental.service;

import com.rental.dto.MaintenanceRequest;
import com.rental.entity.Maintenance;
import com.rental.entity.Vehicle;
import com.rental.repository.MaintenanceRepository;
import com.rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public Maintenance scheduleMaintenance(MaintenanceRequest req) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + req.getVehicleId()));

        Maintenance maintenance = new Maintenance();
        maintenance.setVehicle(vehicle);
        maintenance.setLastServiceDate(req.getLastServiceDate() != null ? req.getLastServiceDate() : vehicle.getLastServiceDate());
        maintenance.setNextServiceDate(req.getNextServiceDate());
        maintenance.setMaintenanceStatus(req.getMaintenanceStatus() != null ? req.getMaintenanceStatus() : "SCHEDULED");
        maintenance.setServiceNotes(req.getServiceNotes());

        maintenance = maintenanceRepository.save(maintenance);

        // Update vehicle state
        vehicle.setLastServiceDate(maintenance.getLastServiceDate());
        if ("IN_PROGRESS".equalsIgnoreCase(maintenance.getMaintenanceStatus()) || "SCHEDULED".equalsIgnoreCase(maintenance.getMaintenanceStatus())) {
            vehicle.setAvailability("MAINTENANCE");
        } else if ("COMPLETED".equalsIgnoreCase(maintenance.getMaintenanceStatus())) {
            vehicle.setAvailability("AVAILABLE");
        }
        vehicleRepository.save(vehicle);

        return maintenance;
    }

    @Transactional
    public Maintenance updateMaintenanceStatus(Long maintenanceId, String status) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found"));

        maintenance.setMaintenanceStatus(status);
        if ("COMPLETED".equalsIgnoreCase(status)) {
            maintenance.setLastServiceDate(LocalDate.now());
            Vehicle v = maintenance.getVehicle();
            v.setLastServiceDate(LocalDate.now());
            v.setAvailability("AVAILABLE");
            vehicleRepository.save(v);
        }
        return maintenanceRepository.save(maintenance);
    }

    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getVehicleMaintenance(Long vehicleId) {
        return maintenanceRepository.findByVehicleVehicleId(vehicleId);
    }

    public List<Map<String, Object>> getExpiryNotifications() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Map<String, Object>> notifications = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate warningThreshold = today.plusDays(30);

        for (Vehicle v : vehicles) {
            if (v.getInsuranceExpiry() != null && v.getInsuranceExpiry().isBefore(warningThreshold)) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("vehicleId", v.getVehicleId());
                notif.put("vehicleDetails", v.getBrand() + " " + v.getModel() + " (" + v.getRegistrationNo() + ")");
                notif.put("type", "Insurance Expiry");
                notif.put("expiryDate", v.getInsuranceExpiry().toString());
                notif.put("isExpired", v.getInsuranceExpiry().isBefore(today));
                notifications.add(notif);
            }
            if (v.getPollutionExpiry() != null && v.getPollutionExpiry().isBefore(warningThreshold)) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("vehicleId", v.getVehicleId());
                notif.put("vehicleDetails", v.getBrand() + " " + v.getModel() + " (" + v.getRegistrationNo() + ")");
                notif.put("type", "Pollution Certificate Expiry");
                notif.put("expiryDate", v.getPollutionExpiry().toString());
                notif.put("isExpired", v.getPollutionExpiry().isBefore(today));
                notifications.add(notif);
            }
        }
        return notifications;
    }
}
