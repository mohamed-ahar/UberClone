package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.Location;
import com.maharram.uberclone.models.PaymentMethod;
import com.maharram.uberclone.models.PaymentStatus;
import com.maharram.uberclone.models.Ride;
import com.maharram.uberclone.models.RideStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RideDTO {

    public record CreateRequest(
        @NotNull(message = "Rider ID is required") UUID riderId,
        @NotNull(message = "Pickup location is required") Location pickupLocation,
        @NotNull(message = "Dropoff location is required") Location dropoffLocation,
        PaymentMethod paymentMethod
    ) {}

    public record AssignDriverRequest(
        @NotNull(message = "Driver ID is required") UUID driverId
    ) {}

    public record StartRideRequest(
        String otp
    ) {}

    public record CompleteRequest(
        BigDecimal fare,
        Double distance,
        Integer duration
    ) {}

    public record CancelRideRequest(
        String reason
    ) {}

    public record Response(
        UUID id,
        UUID riderId,
        String riderName,
        UUID driverId,
        String driverName,
        Location pickupLocation,
        Location dropoffLocation,
        RideStatus status,
        BigDecimal fare,
        Double distance,
        Integer duration,
        String otp,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime requestedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime cancelledAt,
        String cancellationReason
    ) {
        public static Response fromEntity(Ride ride) {
            if (ride == null) return null;
            return new Response(
                ride.getId(),
                ride.getRider() != null ? ride.getRider().getId() : null,
                ride.getRider() != null ? ride.getRider().getFirstName() + " " + ride.getRider().getLastName() : null,
                ride.getDriver() != null ? ride.getDriver().getId() : null,
                ride.getDriver() != null ? ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName() : null,
                ride.getPickupLocation(),
                ride.getDropoffLocation(),
                ride.getStatus(),
                ride.getFare(),
                ride.getDistance(),
                ride.getDuration(),
                ride.getOtp(),
                ride.getPaymentMethod(),
                ride.getPaymentStatus(),
                ride.getRequestedAt(),
                ride.getStartedAt(),
                ride.getCompletedAt(),
                ride.getCancelledAt(),
                ride.getCancellationReason()
            );
        }
    }
}
