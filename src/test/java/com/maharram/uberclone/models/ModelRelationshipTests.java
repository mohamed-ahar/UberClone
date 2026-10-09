package com.maharram.uberclone.models;

import com.maharram.uberclone.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ModelRelationshipTests {

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void testRideLifecycleAndRelationships() {
        // Create Rider
        Rider rider = Rider.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe." + UUID.randomUUID() + "@example.com")
                .password("securePassword123")
                .age(25)
                .role("USER_ROLE")
                .preferredPaymentMethod(PaymentMethod.CREDIT_CARD)
                .walletBalance(100.0)
                .build();
        rider = riderRepository.save(rider);
        assertNotNull(rider.getId());

        // Create Driver
        Location driverLocation = Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("Times Square")
                .city("New York")
                .build();

        Driver driver = Driver.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith." + UUID.randomUUID() + "@example.com")
                .password("securePassword123")
                .age(30)
                .role("DRIVER_ROLE")
                .carType("Sedan")
                .carNumber("NYC-1234")
                .carModel("Toyota Camry")
                .carColor("Silver")
                .licenseNumber("DL987654")
                .driverStatus(DriverStatus.AVAILABLE)
                .vehicleType(VehicleType.UBER_X)
                .isAvailable(true)
                .location(driverLocation)
                .build();
        driver = driverRepository.save(driver);
        assertNotNull(driver.getId());

        // Create Ride
        Location pickup = new Location(40.7128, -74.0060, "123 Main St", "New York");
        Location dropoff = new Location(40.7589, -73.9851, "456 Broadway", "New York");

        Ride ride = Ride.builder()
                .rider(rider)
                .driver(driver)
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .status(RideStatus.REQUESTED)
                .fare(BigDecimal.valueOf(25.50))
                .distance(5.2)
                .duration(15)
                .otp("4321")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        ride = rideRepository.save(ride);
        assertNotNull(ride.getId());
        assertEquals(RideStatus.REQUESTED, ride.getStatus());

        // Create Payment
        Payment payment = Payment.builder()
                .ride(ride)
                .amount(BigDecimal.valueOf(25.50))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .transactionId("TXN-" + UUID.randomUUID())
                .build();
        payment = paymentRepository.save(payment);
        assertNotNull(payment.getId());

        // Create Review
        Review review = Review.builder()
                .ride(ride)
                .rider(rider)
                .driver(driver)
                .rating(5)
                .comment("Excellent and smooth ride!")
                .build();
        review = reviewRepository.save(review);
        assertNotNull(review.getId());

        // Query back
        var riderRides = rideRepository.findByRider(rider);
        assertEquals(1, riderRides.size());

        var driverRides = rideRepository.findByDriver(driver);
        assertEquals(1, driverRides.size());

        var ridePayment = paymentRepository.findByRide(ride);
        assertTrue(ridePayment.isPresent());
        assertEquals(PaymentStatus.COMPLETED, ridePayment.get().getStatus());

        var rideReview = reviewRepository.findByRide(ride);
        assertTrue(rideReview.isPresent());
        assertEquals(5, rideReview.get().getRating());
    }
}
