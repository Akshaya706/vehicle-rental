package com.rental.controller;

import com.rental.entity.Review;
import com.rental.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> body) {
        try {
            Long customerId = Long.valueOf(body.get("customerId").toString());
            Long vehicleId = Long.valueOf(body.get("vehicleId").toString());
            Integer rating = Integer.valueOf(body.get("rating").toString());
            String comment = body.get("comment") != null ? body.get("comment").toString() : "";

            Review review = reviewService.addReview(customerId, vehicleId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getVehicleReviews(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reviewService.getVehicleRatingSummary(vehicleId));
    }
}
