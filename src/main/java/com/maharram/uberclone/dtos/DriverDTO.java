package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.DriverStatus;
import com.maharram.uberclone.models.Location;
import com.maharram.uberclone.models.VehicleType;
import com.maharram.uberclone.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class DriverDTO {

    public record CreateRequest(
        @NotBlank(message = "FirstName is required") String firstname,
        @NotBlank(message = "LastName is required") String lastname,
        @NotNull(message = "Age is required") @Min(value = 18, message = "Driver must be at least 18") Integer age,
        @Email(message = "Invalid email format") String email,
        @ValidPassword String password,
        String phoneNumber,
        String carType,
        @NotBlank(message = "Car number is required") String carNumber,
        String carModel,
        String carColor,
        @NotBlank(message = "License number is required") String licenseNumber,
        VehicleType vehicleType,
        Location location
    ) {}

    public record UpdateRequest(
        String firstname,
        String lastname,
        Integer age,
        String phoneNumber,
        String carType,
        String carNumber,
        String carModel,
        String carColor,
        String licenseNumber,
        VehicleType vehicleType,
        Boolean isAvailable,
        DriverStatus driverStatus,
        Location location
    ) {}

    public record UpdateLocationRequest(
        @NotNull(message = "Location is required") Location location
    ) {}

    public record UpdateStatusRequest(
        @NotNull(message = "Status is required") DriverStatus status
    ) {}

    public record UpdateAvailabilityRequest(
        @NotNull(message = "Availability is required") Boolean isAvailable
    ) {}

    public record Response(
        UUID id,
        String firstname,
        String lastname,
        Integer age,
        String email,
        String phoneNumber,
        String role,
        String carType,
        String carNumber,
        String carModel,
        String carColor,
        String licenseNumber,
        DriverStatus driverStatus,
        VehicleType vehicleType,
        Boolean isAvailable,
        Double rating,
        Integer totalRides,
        Location location
    ) {
        public static Response fromEntity(Driver driver) {
            if (driver == null) return null;
            return new Response(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getAge(),
                driver.getEmail(),
                driver.getPhoneNumber(),
                driver.getRole(),
                driver.getCarType(),
                driver.getCarNumber(),
                driver.getCarModel(),
                driver.getCarColor(),
                driver.getLicenseNumber(),
                driver.getDriverStatus(),
                driver.getVehicleType(),
                driver.getIsAvailable(),
                driver.getRating(),
                driver.getTotalRides(),
                driver.getLocation()
            );
        }
    }
}
