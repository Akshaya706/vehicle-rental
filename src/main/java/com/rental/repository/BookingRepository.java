package com.rental.repository;

import com.rental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerCustomerId(Long customerId);

    List<Booking> findByVehicleVehicleId(Long vehicleId);

    List<Booking> findByBookingStatus(String bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.vehicleId = :vehicleId " +
           "AND b.bookingStatus NOT IN ('CANCELLED', 'REJECTED', 'COMPLETED') " +
           "AND (:excludeBookingId IS NULL OR b.bookingId != :excludeBookingId) " +
           "AND b.rentalStartDate <= :endDate AND b.rentalEndDate >= :startDate")
    List<Booking> findOverlappingBookings(@Param("vehicleId") Long vehicleId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("excludeBookingId") Long excludeBookingId);
}
