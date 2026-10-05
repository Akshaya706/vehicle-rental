package com.rental.dto;

import java.time.LocalDate;

public class BookingRequest {
    private Long customerId;
    private Long vehicleId;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private Boolean isWeekendOrFestival = false;
    private String notes;

    public BookingRequest() {}

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public LocalDate getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(LocalDate rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public Boolean getIsWeekendOrFestival() {
        return isWeekendOrFestival;
    }

    public void setIsWeekendOrFestival(Boolean weekendOrFestival) {
        isWeekendOrFestival = weekendOrFestival;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
