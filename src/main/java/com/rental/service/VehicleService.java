package com.rental.service;

import com.rental.entity.Vehicle;
import com.rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicleRepository.findByRegistrationNo(vehicle.getRegistrationNo()).isPresent()) {
            throw new IllegalArgumentException("Vehicle with registration number " + vehicle.getRegistrationNo() + " already exists.");
        }
        if (vehicle.getAvailability() == null || vehicle.getAvailability().isEmpty()) {
            vehicle.setAvailability("AVAILABLE");
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, Vehicle updatedVehicle) {
        Vehicle existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        existing.setVehicleType(updatedVehicle.getVehicleType());
        existing.setBrand(updatedVehicle.getBrand());
        existing.setModel(updatedVehicle.getModel());
        existing.setRegistrationNo(updatedVehicle.getRegistrationNo());
        existing.setDailyRate(updatedVehicle.getDailyRate());
        existing.setAvailability(updatedVehicle.getAvailability());
        existing.setLastServiceDate(updatedVehicle.getLastServiceDate());
        existing.setInsuranceExpiry(updatedVehicle.getInsuranceExpiry());
        existing.setPollutionExpiry(updatedVehicle.getPollutionExpiry());
        existing.setFuelType(updatedVehicle.getFuelType());
        existing.setSeatingCapacity(updatedVehicle.getSeatingCapacity());
        if (updatedVehicle.getImageUrl() != null && !updatedVehicle.getImageUrl().isEmpty()) {
            existing.setImageUrl(updatedVehicle.getImageUrl());
        }
        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public List<Vehicle> searchVehicles(String vehicleType, String brand, String model, String availability, Double minPrice, Double maxPrice) {
        return vehicleRepository.searchVehicles(vehicleType, brand, model, availability, minPrice, maxPrice);
    }

    public Vehicle updateAvailability(Long vehicleId, String availability) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));
        vehicle.setAvailability(availability);
        return vehicleRepository.save(vehicle);
    }
}
