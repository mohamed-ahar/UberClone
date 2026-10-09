package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.ReviewDTO;
import com.maharram.uberclone.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO.Response> createReview(@Valid @RequestBody ReviewDTO.CreateRequest request) {
        ReviewDTO.Response response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO.Response> getReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ReviewDTO.Response> getReviewByRide(@PathVariable UUID rideId) {
        return ResponseEntity.ok(reviewService.getReviewByRide(rideId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ReviewDTO.Response>> getReviewsByDriver(@PathVariable UUID driverId) {
        return ResponseEntity.ok(reviewService.getReviewsByDriver(driverId));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<ReviewDTO.Response>> getReviewsByRider(@PathVariable UUID riderId) {
        return ResponseEntity.ok(reviewService.getReviewsByRider(riderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO.Response> updateReview(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewDTO.UpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
