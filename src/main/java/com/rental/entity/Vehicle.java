package com.rental.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType; // Car, Bike, SUV, Sedan, Luxury, Electric

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "registration_no", unique = true, nullable = false)
    private String registrationNo;

    @Column(name = "daily_rate", nullable = false)
    private Double dailyRate;

    @Column(name = "availability", nullable = false)
    private String availability; // AVAILABLE, BOOKED, RENTED, MAINTENANCE

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "insurance_expiry")
    private LocalDate insuranceExpiry;

    @Column(name = "pollution_expiry")
    private LocalDate pollutionExpiry;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "image_url")
    private String imageUrl;

    public Vehicle() {}

    public Vehicle(String vehicleType, String brand, String model, String registrationNo, Double dailyRate, String availability, LocalDate lastServiceDate, LocalDate insuranceExpiry, LocalDate pollutionExpiry, String fuelType, Integer seatingCapacity, String imageUrl) {
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.registrationNo = registrationNo;
        this.dailyRate = dailyRate;
        this.availability = availability;
        this.lastServiceDate = lastServiceDate;
        this.insuranceExpiry = insuranceExpiry;
        this.pollutionExpiry = pollutionExpiry;
        this.fuelType = fuelType;
        this.seatingCapacity = seatingCapacity;
        this.imageUrl = imageUrl;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public LocalDate getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(LocalDate insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public LocalDate getPollutionExpiry() {
        return pollutionExpiry;
    }

    public void setPollutionExpiry(LocalDate pollutionExpiry) {
        this.pollutionExpiry = pollutionExpiry;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
