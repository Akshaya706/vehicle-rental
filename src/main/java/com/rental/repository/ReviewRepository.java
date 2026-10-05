package com.rental.repository;

import com.rental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVehicleVehicleId(Long vehicleId);
    List<Review> findByCustomerCustomerId(Long customerId);
}
