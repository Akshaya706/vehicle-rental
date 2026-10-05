package com.rental.repository;

import com.rental.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    Optional<DamageReport> findByBookingBookingId(Long bookingId);
}
