package com.rental.service;

import com.rental.dto.VehicleReturnRequest;
import com.rental.entity.Booking;
import com.rental.entity.DamageReport;
import com.rental.entity.Vehicle;
import com.rental.repository.BookingRepository;
import com.rental.repository.DamageReportRepository;
import com.rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DamageService {

    @Autowired
    private DamageReportRepository damageReportRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public Map<String, Object> processVehicleReturn(VehicleReturnRequest req) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + req.getBookingId()));

        LocalDate returnDate = req.getReturnDate() != null ? req.getReturnDate() : LocalDate.now();

        // Calculate late return charges
        double lateFee = 0.0;
        long lateDays = 0;
        if (returnDate.isAfter(booking.getRentalEndDate())) {
            lateDays = ChronoUnit.DAYS.between(booking.getRentalEndDate(), returnDate);
            // Late fee: 1.5x daily rate per late day
            lateFee = lateDays * (booking.getDailyRate() * 1.5);
        }

        // Damage inspection check
        DamageReport damageReport = null;
        double repairCost = req.getRepairCost() != null ? req.getRepairCost() : 0.0;
        if ((req.getDamageDescription() != null && !req.getDamageDescription().trim().isEmpty()) || repairCost > 0) {
            damageReport = new DamageReport();
            damageReport.setBooking(booking);
            damageReport.setDamageDescription(req.getDamageDescription());
            damageReport.setRepairCost(repairCost);
            damageReport.setInspectionDate(returnDate);
            damageReport.setInspectorName(req.getInspectorName() != null ? req.getInspectorName() : "Rental Manager");
            damageReport.setOdometerReading(req.getOdometerReading());
            damageReport.setFuelLevel(req.getFuelLevel());

            damageReport = damageReportRepository.save(damageReport);
        }

        double extraCharges = booking.getExtraCharges() + lateFee + repairCost;
        double finalTotalCharge = booking.getTotalCharge() + lateFee + repairCost;

        booking.setExtraCharges(extraCharges);
        booking.setTotalCharge(finalTotalCharge);
        booking.setBookingStatus("COMPLETED");
        bookingRepository.save(booking);

        Vehicle vehicle = booking.getVehicle();
        if (repairCost > 500) {
            vehicle.setAvailability("MAINTENANCE"); // High damage sends vehicle to maintenance
        } else {
            vehicle.setAvailability("AVAILABLE");
        }
        vehicleRepository.save(vehicle);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Vehicle Returned Successfully");
        result.put("bookingId", booking.getBookingId());
        result.put("returnDate", returnDate.toString());
        result.put("lateDays", lateDays);
        result.put("lateFee", lateFee);
        result.put("repairCost", repairCost);
        result.put("finalTotalBill", finalTotalCharge);
        result.put("vehicleAvailability", vehicle.getAvailability());
        if (damageReport != null) {
            result.put("damageReportId", damageReport.getDamageId());
            result.put("damageDescription", damageReport.getDamageDescription());
        }

        return result;
    }

    public List<DamageReport> getAllDamageReports() {
        return damageReportRepository.findAll();
    }
}
