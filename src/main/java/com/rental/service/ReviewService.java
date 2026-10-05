package com.rental.service;

import com.rental.entity.Customer;
import com.rental.entity.Review;
import com.rental.entity.Vehicle;
import com.rental.repository.CustomerRepository;
import com.rental.repository.ReviewRepository;
import com.rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Review addReview(Long customerId, Long vehicleId, Integer rating, String comment) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        Review review = new Review(vehicle, customer, rating, comment, LocalDate.now());
        return reviewRepository.save(review);
    }

    public List<Review> getVehicleReviews(Long vehicleId) {
        return reviewRepository.findByVehicleVehicleId(vehicleId);
    }

    public Map<String, Object> getVehicleRatingSummary(Long vehicleId) {
        List<Review> reviews = reviewRepository.findByVehicleVehicleId(vehicleId);
        double avg = 0.0;
        if (!reviews.isEmpty()) {
            avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("vehicleId", vehicleId);
        summary.put("averageRating", Math.round(avg * 10.0) / 10.0);
        summary.put("reviewCount", reviews.size());
        summary.put("reviews", reviews);
        return summary;
    }
}
