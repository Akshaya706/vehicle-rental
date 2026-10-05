package com.rental.dto;

public class PriceCalculationResponse {
    private Long vehicleId;
    private String vehicleType;
    private String brandAndModel;
    private int rentalDays;
    private double dailyRate;
    private double dynamicMultiplier;
    private double dynamicPrice;
    private double extraCharges;
    private double totalRentalCharge;

    public PriceCalculationResponse() {}

    public PriceCalculationResponse(Long vehicleId, String vehicleType, String brandAndModel, int rentalDays, double dailyRate, double dynamicMultiplier, double dynamicPrice, double extraCharges, double totalRentalCharge) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.brandAndModel = brandAndModel;
        this.rentalDays = rentalDays;
        this.dailyRate = dailyRate;
        this.dynamicMultiplier = dynamicMultiplier;
        this.dynamicPrice = dynamicPrice;
        this.extraCharges = extraCharges;
        this.totalRentalCharge = totalRentalCharge;
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

    public String getBrandAndModel() {
        return brandAndModel;
    }

    public void setBrandAndModel(String brandAndModel) {
        this.brandAndModel = brandAndModel;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public double getDynamicMultiplier() {
        return dynamicMultiplier;
    }

    public void setDynamicMultiplier(double dynamicMultiplier) {
        this.dynamicMultiplier = dynamicMultiplier;
    }

    public double getDynamicPrice() {
        return dynamicPrice;
    }

    public void setDynamicPrice(double dynamicPrice) {
        this.dynamicPrice = dynamicPrice;
    }

    public double getExtraCharges() {
        return extraCharges;
    }

    public void setExtraCharges(double extraCharges) {
        this.extraCharges = extraCharges;
    }

    public double getTotalRentalCharge() {
        return totalRentalCharge;
    }

    public void setTotalRentalCharge(double totalRentalCharge) {
        this.totalRentalCharge = totalRentalCharge;
    }
}
