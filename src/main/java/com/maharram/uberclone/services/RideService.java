package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.RideDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.*;
import com.maharram.uberclone.repositories.DriverRepository;
import com.maharram.uberclone.repositories.RideRepository;
import com.maharram.uberclone.repositories.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideService(RideRepository rideRepository, RiderRepository riderRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    // CREATE (Request a Ride)
    @Transactional
    public RideDTO.Response createRide(RideDTO.CreateRequest request) {
        Rider rider = this.riderRepository.findById(request.riderId())
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + request.riderId()));

        String otp = String.format("%04d", new Random().nextInt(10000));

        Ride ride = Ride.builder()
                .rider(rider)
                .pickupLocation(request.pickupLocation())
                .dropoffLocation(request.dropoffLocation())
                .status(RideStatus.REQUESTED)
                .otp(otp)
                .paymentMethod(request.paymentMethod() != null ? request.paymentMethod() : rider.getPreferredPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        Ride saved = this.rideRepository.save(ride);
        return RideDTO.Response.fromEntity(saved);
    }

    // READ by ID
    @Transactional(readOnly = true)
    public RideDTO.Response getRideById(UUID id) {
        Ride ride = this.rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + id));
        return RideDTO.Response.fromEntity(ride);
    }

    // READ by Rider
    @Transactional(readOnly = true)
    public List<RideDTO.Response> getRidesByRider(UUID riderId) {
        Rider rider = this.riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + riderId));
        return this.rideRepository.findByRider(rider).stream()
                .map(RideDTO.Response::fromEntity)
                .toList();
    }

    // READ by Driver
    @Transactional(readOnly = true)
    public List<RideDTO.Response> getRidesByDriver(UUID driverId) {
        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        return this.rideRepository.findByDriver(driver).stream()
                .map(RideDTO.Response::fromEntity)
                .toList();
    }

    // READ by Status
    @Transactional(readOnly = true)
    public List<RideDTO.Response> getRidesByStatus(RideStatus status) {
        return this.rideRepository.findByStatus(status).stream()
                .map(RideDTO.Response::fromEntity)
                .toList();
    }

    // ASSIGN DRIVER (Accept Ride)
    @Transactional
    public RideDTO.Response assignDriver(UUID rideId, UUID driverId) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException("Ride cannot be assigned in status: " + ride.getStatus());
        }

        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        if (!Boolean.TRUE.equals(driver.getIsAvailable()) || driver.getDriverStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver is not available");
        }

        driver.setIsAvailable(false);
        driver.setDriverStatus(DriverStatus.BUSY);
        this.driverRepository.save(driver);

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);
        Ride updated = this.rideRepository.save(ride);
        return RideDTO.Response.fromEntity(updated);
    }

    // START RIDE
    @Transactional
    public RideDTO.Response startRide(UUID rideId, String otp) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getOtp() != null && !ride.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP verification code");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());
        Ride updated = this.rideRepository.save(ride);
        return RideDTO.Response.fromEntity(updated);
    }

    // COMPLETE RIDE
    @Transactional
    public RideDTO.Response completeRide(UUID rideId, RideDTO.CompleteRequest request) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        if (request != null) {
            if (request.fare() != null) ride.setFare(request.fare());
            if (request.distance() != null) ride.setDistance(request.distance());
            if (request.duration() != null) ride.setDuration(request.duration());
        }

        // Release driver
        if (ride.getDriver() != null) {
            Driver driver = ride.getDriver();
            driver.setIsAvailable(true);
            driver.setDriverStatus(DriverStatus.AVAILABLE);
            driver.setTotalRides(driver.getTotalRides() != null ? driver.getTotalRides() + 1 : 1);
            this.driverRepository.save(driver);
        }

        Ride updated = this.rideRepository.save(ride);
        return RideDTO.Response.fromEntity(updated);
    }

    // CANCEL RIDE
    @Transactional
    public RideDTO.Response cancelRide(UUID rideId, String reason) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(reason);

        // Release driver if one was assigned
        if (ride.getDriver() != null) {
            Driver driver = ride.getDriver();
            driver.setIsAvailable(true);
            driver.setDriverStatus(DriverStatus.AVAILABLE);
            this.driverRepository.save(driver);
        }

        Ride updated = this.rideRepository.save(ride);
        return RideDTO.Response.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void deleteRide(UUID id) {
        if (!this.rideRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ride not found with id: " + id);
        }
        this.rideRepository.deleteById(id);
    }
}
