package com.rental.dto;

import java.time.LocalDate;

public class PriceCalculationRequest {
    private Long vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isWeekendOrFestival = false;
    private Double extraCharges = 0.0;

    public PriceCalculationRequest() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsWeekendOrFestival() {
        return isWeekendOrFestival;
    }

    public void setIsWeekendOrFestival(Boolean weekendOrFestival) {
        isWeekendOrFestival = weekendOrFestival;
    }

    public Double getExtraCharges() {
        return extraCharges;
    }

    public void setExtraCharges(Double extraCharges) {
        this.extraCharges = extraCharges;
    }
}
