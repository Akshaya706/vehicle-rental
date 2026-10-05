package com.rental.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsSummaryDTO {
    private double totalRevenue;
    private double monthlyRevenue;
    private long totalVehicles;
    private long availableVehicles;
    private long activeRentals;
    private long underMaintenance;
    private double utilizationRate;
    private long totalBookings;
    private long totalCustomers;

    private List<Map<String, Object>> mostRentedVehicles;
    private List<Map<String, Object>> topCustomers;
    private List<Map<String, Object>> maintenanceStats;
    private List<Map<String, Object>> monthlyRevenueTrend;

    public AnalyticsSummaryDTO() {}

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(long availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public long getActiveRentals() {
        return activeRentals;
    }

    public void setActiveRentals(long activeRentals) {
        this.activeRentals = activeRentals;
    }

    public long getUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(long underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public double getUtilizationRate() {
        return utilizationRate;
    }

    public void setUtilizationRate(double utilizationRate) {
        this.utilizationRate = utilizationRate;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public List<Map<String, Object>> getMostRentedVehicles() {
        return mostRentedVehicles;
    }

    public void setMostRentedVehicles(List<Map<String, Object>> mostRentedVehicles) {
        this.mostRentedVehicles = mostRentedVehicles;
    }

    public List<Map<String, Object>> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<Map<String, Object>> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public List<Map<String, Object>> getMaintenanceStats() {
        return maintenanceStats;
    }

    public void setMaintenanceStats(List<Map<String, Object>> maintenanceStats) {
        this.maintenanceStats = maintenanceStats;
    }

    public List<Map<String, Object>> getMonthlyRevenueTrend() {
        return monthlyRevenueTrend;
    }

    public void setMonthlyRevenueTrend(List<Map<String, Object>> monthlyRevenueTrend) {
        this.monthlyRevenueTrend = monthlyRevenueTrend;
    }
}
