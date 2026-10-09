package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.Payment;
import com.maharram.uberclone.models.PaymentMethod;
import com.maharram.uberclone.models.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDTO {

    public record CreateRequest(
        @NotNull(message = "Ride ID is required") UUID rideId,
        @NotNull(message = "Amount is required") BigDecimal amount,
        @NotNull(message = "Payment method is required") PaymentMethod paymentMethod,
        String transactionId
    ) {}

    public record UpdateStatusRequest(
        @NotNull(message = "Status is required") PaymentStatus status
    ) {}

    public record Response(
        UUID id,
        UUID rideId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionId,
        LocalDateTime paymentDate
    ) {
        public static Response fromEntity(Payment payment) {
            if (payment == null) return null;
            return new Response(
                payment.getId(),
                payment.getRide() != null ? payment.getRide().getId() : null,
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate()
            );
        }
    }
}
