package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDTO {

    public record CreateRequest(
        @NotNull(message = "Ride ID is required") UUID rideId,
        @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating cannot exceed 5") Integer rating,
        String comment
    ) {}

    public record UpdateRequest(
        @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating cannot exceed 5") Integer rating,
        String comment
    ) {}

    public record Response(
        UUID id,
        UUID rideId,
        UUID riderId,
        String riderName,
        UUID driverId,
        String driverName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
    ) {
        public static Response fromEntity(Review review) {
            if (review == null) return null;
            return new Response(
                review.getId(),
                review.getRide() != null ? review.getRide().getId() : null,
                review.getRider() != null ? review.getRider().getId() : null,
                review.getRider() != null ? review.getRider().getFirstName() + " " + review.getRider().getLastName() : null,
                review.getDriver() != null ? review.getDriver().getId() : null,
                review.getDriver() != null ? review.getDriver().getFirstName() + " " + review.getDriver().getLastName() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
            );
        }
    }
}
