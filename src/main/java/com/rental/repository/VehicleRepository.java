package com.rental.repository;

import com.rental.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByRegistrationNo(String registrationNo);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:vehicleType IS NULL OR LOWER(v.vehicleType) LIKE LOWER(CONCAT('%', :vehicleType, '%'))) AND " +
           "(:brand IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
           "(:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:availability IS NULL OR v.availability = :availability) AND " +
           "(:minPrice IS NULL OR v.dailyRate >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.dailyRate <= :maxPrice)")
    List<Vehicle> searchVehicles(@Param("vehicleType") String vehicleType,
                                 @Param("brand") String brand,
                                 @Param("model") String model,
                                 @Param("availability") String availability,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice);
}
