package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.PaymentMethod;
import com.maharram.uberclone.models.Rider;
import com.maharram.uberclone.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RiderDTO {

    public record CreateRequest(
        @NotBlank(message = "FirstName is required") String firstname,
        @NotBlank(message = "LastName is required") String lastname,
        @NotNull(message = "Age is required") @Min(value = 18, message = "Rider must be at least 18") Integer age,
        @Email(message = "Invalid email format") String email,
        @ValidPassword String password,
        String phoneNumber,
        PaymentMethod preferredPaymentMethod,
        Double walletBalance
    ) {}

    public record UpdateRequest(
        String firstname,
        String lastname,
        Integer age,
        String phoneNumber,
        PaymentMethod preferredPaymentMethod,
        Double walletBalance
    ) {}

    public record UpdateWalletRequest(
        @NotNull(message = "Amount is required") Double amount
    ) {}

    public record Response(
        UUID id,
        String firstname,
        String lastname,
        Integer age,
        String email,
        String phoneNumber,
        String role,
        Double rating,
        PaymentMethod preferredPaymentMethod,
        Double walletBalance
    ) {
        public static Response fromEntity(Rider rider) {
            if (rider == null) return null;
            return new Response(
                rider.getId(),
                rider.getFirstName(),
                rider.getLastName(),
                rider.getAge(),
                rider.getEmail(),
                rider.getPhoneNumber(),
                rider.getRole(),
                rider.getRating(),
                rider.getPreferredPaymentMethod(),
                rider.getWalletBalance()
            );
        }
    }
}
