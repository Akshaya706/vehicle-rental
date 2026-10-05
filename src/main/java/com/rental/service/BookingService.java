package com.rental.service;

import com.rental.dto.BookingRequest;
import com.rental.dto.PriceCalculationRequest;
import com.rental.dto.PriceCalculationResponse;
import com.rental.entity.Booking;
import com.rental.entity.Customer;
import com.rental.entity.Vehicle;
import com.rental.repository.BookingRepository;
import com.rental.repository.CustomerRepository;
import com.rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public PriceCalculationResponse calculateRentalCharge(PriceCalculationRequest req) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        long days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        if (days <= 0) days = 1;

        double baseRate = vehicle.getDailyRate();

        // Dynamic multiplier logic
        double multiplier = 1.0;
        if (Boolean.TRUE.equals(req.getIsWeekendOrFestival())) {
            multiplier = 1.25; // 25% surge for weekends/festivals
        } else {
            // Auto check if date range covers weekend (Saturday/Sunday)
            LocalDate temp = req.getStartDate();
            while (!temp.isAfter(req.getEndDate())) {
                if (temp.getDayOfWeek() == DayOfWeek.SATURDAY || temp.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    multiplier = 1.15; // 15% weekend surcharge
                    break;
                }
                temp = temp.plusDays(1);
            }
        }

        // Additional factor based on vehicle type
        if ("Luxury".equalsIgnoreCase(vehicle.getVehicleType())) {
            multiplier += 0.10;
        }

        double extraCharges = req.getExtraCharges() != null ? req.getExtraCharges() : 0.0;
        double dynamicPrice = baseRate * multiplier;
        double totalCharge = (dynamicPrice * days) + extraCharges;

        return new PriceCalculationResponse(
                vehicle.getVehicleId(),
                vehicle.getVehicleType(),
                vehicle.getBrand() + " " + vehicle.getModel(),
                (int) days,
                baseRate,
                multiplier,
                Math.round(dynamicPrice * 100.0) / 100.0,
                extraCharges,
                Math.round(totalCharge * 100.0) / 100.0
        );
    }

    @Transactional
    public Booking createBooking(BookingRequest req) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Check if vehicle is under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(vehicle.getAvailability())) {
            throw new IllegalStateException("Vehicle is currently under maintenance and cannot be booked.");
        }

        // Prevent overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                req.getVehicleId(), req.getRentalStartDate(), req.getRentalEndDate(), null);

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Vehicle has an overlapping booking for the selected dates.");
        }

        PriceCalculationRequest priceReq = new PriceCalculationRequest();
        priceReq.setVehicleId(req.getVehicleId());
        priceReq.setStartDate(req.getRentalStartDate());
        priceReq.setEndDate(req.getRentalEndDate());
        priceReq.setIsWeekendOrFestival(req.getIsWeekendOrFestival());

        PriceCalculationResponse priceRes = calculateRentalCharge(priceReq);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setBookingDate(LocalDateTime.now());
        booking.setRentalStartDate(req.getRentalStartDate());
        booking.setRentalEndDate(req.getRentalEndDate());
        booking.setBookingStatus("PENDING"); // Requires Rental Manager approval
        booking.setRentalDays(priceRes.getRentalDays());
        booking.setDailyRate(priceRes.getDailyRate());
        booking.setDynamicPricingFactor(priceRes.getDynamicMultiplier());
        booking.setExtraCharges(priceRes.getExtraCharges());
        booking.setTotalCharge(priceRes.getTotalRentalCharge());
        booking.setNotes(req.getNotes());

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setBookingStatus("APPROVED");
        Vehicle vehicle = booking.getVehicle();
        vehicle.setAvailability("BOOKED");
        vehicleRepository.save(vehicle);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rejectBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setBookingStatus("REJECTED");
        if (reason != null) {
            booking.setNotes((booking.getNotes() != null ? booking.getNotes() + " | " : "") + "Rejected: " + reason);
        }
        Vehicle vehicle = booking.getVehicle();
        if ("BOOKED".equalsIgnoreCase(vehicle.getAvailability())) {
            vehicle.setAvailability("AVAILABLE");
            vehicleRepository.save(vehicle);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking extendBooking(Long bookingId, LocalDate newEndDate) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (newEndDate.isBefore(booking.getRentalEndDate()) || newEndDate.isEqual(booking.getRentalEndDate())) {
            throw new IllegalArgumentException("New end date must be after current end date (" + booking.getRentalEndDate() + ")");
        }

        // Overlap check for extension period
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                booking.getVehicle().getVehicleId(), booking.getRentalEndDate().plusDays(1), newEndDate, bookingId);

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Cannot extend: Vehicle is already booked by another customer for the extended period.");
        }

        booking.setRentalEndDate(newEndDate);
        booking.setBookingStatus("EXTENDED");

        // Recalculate charges
        PriceCalculationRequest priceReq = new PriceCalculationRequest();
        priceReq.setVehicleId(booking.getVehicle().getVehicleId());
        priceReq.setStartDate(booking.getRentalStartDate());
        priceReq.setEndDate(newEndDate);

        PriceCalculationResponse priceRes = calculateRentalCharge(priceReq);
        booking.setRentalDays(priceRes.getRentalDays());
        booking.setTotalCharge(priceRes.getTotalRentalCharge());

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setBookingStatus("CANCELLED");
        Vehicle vehicle = booking.getVehicle();
        if (!"MAINTENANCE".equalsIgnoreCase(vehicle.getAvailability())) {
            vehicle.setAvailability("AVAILABLE");
            vehicleRepository.save(vehicle);
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerCustomerId(customerId);
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }
}
