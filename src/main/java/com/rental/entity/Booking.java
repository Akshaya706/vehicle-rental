package com.rental.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(name = "rental_start_date", nullable = false)
    private LocalDate rentalStartDate;

    @Column(name = "rental_end_date", nullable = false)
    private LocalDate rentalEndDate;

    @Column(name = "booking_status", nullable = false)
    private String bookingStatus; // PENDING, APPROVED, REJECTED, CANCELLED, ACTIVE, COMPLETED, EXTENDED

    @Column(name = "rental_days")
    private Integer rentalDays;

    @Column(name = "daily_rate")
    private Double dailyRate;

    @Column(name = "dynamic_pricing_factor")
    private Double dynamicPricingFactor = 1.0;

    @Column(name = "extra_charges")
    private Double extraCharges = 0.0;

    @Column(name = "total_charge", nullable = false)
    private Double totalCharge;

    @Column(name = "notes")
    private String notes;

    public Booking() {}

    public Booking(Customer customer, Vehicle vehicle, LocalDateTime bookingDate, LocalDate rentalStartDate, LocalDate rentalEndDate, String bookingStatus, Integer rentalDays, Double dailyRate, Double dynamicPricingFactor, Double extraCharges, Double totalCharge) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.bookingDate = bookingDate;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.bookingStatus = bookingStatus;
        this.rentalDays = rentalDays;
        this.dailyRate = dailyRate;
        this.dynamicPricingFactor = dynamicPricingFactor;
        this.extraCharges = extraCharges;
        this.totalCharge = totalCharge;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
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

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getDynamicPricingFactor() {
        return dynamicPricingFactor;
    }

    public void setDynamicPricingFactor(Double dynamicPricingFactor) {
        this.dynamicPricingFactor = dynamicPricingFactor;
    }

    public Double getExtraCharges() {
        return extraCharges;
    }

    public void setExtraCharges(Double extraCharges) {
        this.extraCharges = extraCharges;
    }

    public Double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(Double totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
