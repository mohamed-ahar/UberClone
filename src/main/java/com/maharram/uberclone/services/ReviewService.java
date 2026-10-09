package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.ReviewDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.Review;
import com.maharram.uberclone.models.Ride;
import com.maharram.uberclone.models.Rider;
import com.maharram.uberclone.repositories.DriverRepository;
import com.maharram.uberclone.repositories.ReviewRepository;
import com.maharram.uberclone.repositories.RideRepository;
import com.maharram.uberclone.repositories.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         RideRepository rideRepository,
                         DriverRepository driverRepository,
                         RiderRepository riderRepository) {
        this.reviewRepository = reviewRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    // CREATE
    @Transactional
    public ReviewDTO.Response createReview(ReviewDTO.CreateRequest request) {
        Ride ride = this.rideRepository.findById(request.rideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + request.rideId()));

        Review review = Review.builder()
                .ride(ride)
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .rating(request.rating())
                .comment(request.comment())
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = this.reviewRepository.save(review);

        // Update driver's average rating if driver exists
        if (ride.getDriver() != null) {
            updateDriverAverageRating(ride.getDriver());
        }

        return ReviewDTO.Response.fromEntity(saved);
    }

    // READ by ID
    @Transactional(readOnly = true)
    public ReviewDTO.Response getReviewById(UUID id) {
        Review review = this.reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return ReviewDTO.Response.fromEntity(review);
    }

    // READ by Ride
    @Transactional(readOnly = true)
    public ReviewDTO.Response getReviewByRide(UUID rideId) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        Review review = this.reviewRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for ride id: " + rideId));
        return ReviewDTO.Response.fromEntity(review);
    }

    // READ by Driver
    @Transactional(readOnly = true)
    public List<ReviewDTO.Response> getReviewsByDriver(UUID driverId) {
        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        return this.reviewRepository.findByDriver(driver).stream()
                .map(ReviewDTO.Response::fromEntity)
                .toList();
    }

    // READ by Rider
    @Transactional(readOnly = true)
    public List<ReviewDTO.Response> getReviewsByRider(UUID riderId) {
        Rider rider = this.riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + riderId));
        return this.reviewRepository.findByRider(rider).stream()
                .map(ReviewDTO.Response::fromEntity)
                .toList();
    }

    // UPDATE
    @Transactional
    public ReviewDTO.Response updateReview(UUID id, ReviewDTO.UpdateRequest request) {
        Review review = this.reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        if (request.rating() != null) {
            review.setRating(request.rating());
        }
        if (request.comment() != null) {
            review.setComment(request.comment());
        }

        Review updated = this.reviewRepository.save(review);

        if (review.getDriver() != null) {
            updateDriverAverageRating(review.getDriver());
        }

        return ReviewDTO.Response.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void deleteReview(UUID id) {
        Review review = this.reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        Driver driver = review.getDriver();
        this.reviewRepository.deleteById(id);

        if (driver != null) {
            updateDriverAverageRating(driver);
        }
    }

    private void updateDriverAverageRating(Driver driver) {
        List<Review> reviews = this.reviewRepository.findByDriver(driver);
        if (!reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(5.0);
            driver.setRating(Math.round(avg * 10.0) / 10.0);
            this.driverRepository.save(driver);
        }
    }
}
