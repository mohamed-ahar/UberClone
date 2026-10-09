package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.*;
import com.maharram.uberclone.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ServiceCrudTests {

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private RideService rideService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReviewService reviewService;

    @Test
    void testUserCrud() {
        String uniqueEmail = "user." + UUID.randomUUID() + "@test.com";

        // Create
        UserDTO.CreateRequest createReq = new UserDTO.CreateRequest(
                "Alice", "Wonderland", 24, uniqueEmail, "SecurePass123!", "1234567890", UserDTO.UserRole.USER_ROLE
        );
        UserDTO.CreateResponse created = userService.create(createReq);
        assertNotNull(created.id());

        // Read by ID
        UserDTO.Response found = userService.findById(created.id());
        assertEquals("Alice", found.firstname());
        assertEquals(24, found.age());

        // Read by Email
        UserDTO.Response byEmail = userService.findByEmail(uniqueEmail);
        assertEquals(created.id(), byEmail.id());

        // Read All
        List<UserDTO.Response> all = userService.findAll();
        assertFalse(all.isEmpty());

        // Update
        UserDTO.UpdateRequest updateReq = new UserDTO.UpdateRequest(
                "Alicia", "Wonder", 25, "0987654321", UserDTO.UserRole.ADMIN_ROLE
        );
        UserDTO.Response updated = userService.update(created.id(), updateReq);
        assertEquals("Alicia", updated.firstname());
        assertEquals(25, updated.age());
        assertEquals("ADMIN_ROLE", updated.role());

        // Delete
        userService.delete(created.id());
        assertThrows(RuntimeException.class, () -> userService.findById(created.id()));
    }

    @Test
    void testDriverCrud() {
        String email = "driver." + UUID.randomUUID() + "@test.com";

        // Create
        DriverDTO.CreateRequest req = new DriverDTO.CreateRequest(
                "Bob", "Builder", 35, email, "SecurePass123!", "5551234567",
                "Sedan", "CAB-789", "Toyota Prius", "White", "DL123456",
                VehicleType.UBER_X, new Location(40.7128, -74.0060, "Times Square", "New York")
        );
        DriverDTO.Response driver = driverService.create(req);
        assertNotNull(driver.id());
        assertEquals(DriverStatus.AVAILABLE, driver.driverStatus());
        assertTrue(driver.isAvailable());

        // Read by ID
        DriverDTO.Response found = driverService.findById(driver.id());
        assertEquals("Toyota Prius", found.carModel());

        // Update Location
        Location newLoc = new Location(40.730610, -73.935242, "Greenpoint", "Brooklyn");
        DriverDTO.Response locUpdated = driverService.updateLocation(driver.id(), newLoc);
        assertEquals("Greenpoint", locUpdated.location().getAddress());

        // Update Status & Availability
        DriverDTO.Response statusUpdated = driverService.updateStatus(driver.id(), DriverStatus.BUSY);
        assertEquals(DriverStatus.BUSY, statusUpdated.driverStatus());
        assertFalse(statusUpdated.isAvailable());

        // Read available
        List<DriverDTO.Response> available = driverService.findAvailableDrivers();
        assertTrue(available.stream().noneMatch(d -> d.id().equals(driver.id())));

        // Update info
        DriverDTO.UpdateRequest updateReq = new DriverDTO.UpdateRequest(
                "Robert", "Builder", 36, "5551234567", "Sedan", "CAB-789",
                "Tesla Model 3", "Red", "DL123456", VehicleType.UBER_COMFORT,
                true, DriverStatus.AVAILABLE, newLoc
        );
        DriverDTO.Response updated = driverService.update(driver.id(), updateReq);
        assertEquals("Tesla Model 3", updated.carModel());
        assertEquals("Red", updated.carColor());

        // Delete
        driverService.delete(driver.id());
        assertThrows(RuntimeException.class, () -> driverService.findById(driver.id()));
    }

    @Test
    void testRiderCrud() {
        String email = "rider." + UUID.randomUUID() + "@test.com";

        // Create
        RiderDTO.CreateRequest req = new RiderDTO.CreateRequest(
                "Charlie", "Brown", 22, email, "SecurePass123!", "5559876543",
                PaymentMethod.WALLET, 50.0
        );
        RiderDTO.Response rider = riderService.create(req);
        assertNotNull(rider.id());
        assertEquals(50.0, rider.walletBalance());

        // Update Wallet Balance
        RiderDTO.Response toppedUp = riderService.updateWalletBalance(rider.id(), 25.0);
        assertEquals(75.0, toppedUp.walletBalance());

        // Update Rider Info
        RiderDTO.UpdateRequest updateReq = new RiderDTO.UpdateRequest(
                "Charles", "Brown", 23, "5559876543", PaymentMethod.CREDIT_CARD, 75.0
        );
        RiderDTO.Response updated = riderService.update(rider.id(), updateReq);
        assertEquals("Charles", updated.firstname());
        assertEquals(PaymentMethod.CREDIT_CARD, updated.preferredPaymentMethod());

        // Delete
        riderService.delete(rider.id());
        assertThrows(RuntimeException.class, () -> riderService.findById(rider.id()));
    }

    @Test
    void testFullRideLifecycleWithPaymentAndReview() {
        // Create Rider
        RiderDTO.Response rider = riderService.create(new RiderDTO.CreateRequest(
                "Dave", "Miller", 28, "dave." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "1112223333", PaymentMethod.CREDIT_CARD, 100.0
        ));

        // Create Driver
        DriverDTO.Response driver = driverService.create(new DriverDTO.CreateRequest(
                "Emma", "Watson", 32, "emma." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "4445556666", "SUV", "UBER-999", "Honda CR-V", "Black", "DL778899",
                VehicleType.UBER_XL, new Location(40.71, -74.00, "Wall St", "NYC")
        ));

        // Request Ride
        RideDTO.CreateRequest rideReq = new RideDTO.CreateRequest(
                rider.id(),
                new Location(40.7128, -74.0060, "Wall St", "NYC"),
                new Location(40.7580, -73.9855, "Central Park", "NYC"),
                PaymentMethod.CREDIT_CARD
        );
        RideDTO.Response ride = rideService.createRide(rideReq);
        assertNotNull(ride.id());
        assertEquals(RideStatus.REQUESTED, ride.status());
        assertNotNull(ride.otp());

        // Driver Accepts Ride
        RideDTO.Response acceptedRide = rideService.assignDriver(ride.id(), driver.id());
        assertEquals(RideStatus.ACCEPTED, acceptedRide.status());
        assertEquals(driver.id(), acceptedRide.driverId());

        // Driver is now busy
        DriverDTO.Response busyDriver = driverService.findById(driver.id());
        assertEquals(DriverStatus.BUSY, busyDriver.driverStatus());

        // Start Ride
        RideDTO.Response startedRide = rideService.startRide(ride.id(), ride.otp());
        assertEquals(RideStatus.IN_PROGRESS, startedRide.status());

        // Complete Ride
        RideDTO.CompleteRequest completeReq = new RideDTO.CompleteRequest(
                BigDecimal.valueOf(35.50), 8.5, 20
        );
        RideDTO.Response completedRide = rideService.completeRide(ride.id(), completeReq);
        assertEquals(RideStatus.COMPLETED, completedRide.status());
        assertEquals(BigDecimal.valueOf(35.50), completedRide.fare());

        // Driver is available again
        DriverDTO.Response freeDriver = driverService.findById(driver.id());
        assertEquals(DriverStatus.AVAILABLE, freeDriver.driverStatus());
        assertEquals(1, freeDriver.totalRides());

        // Create Payment
        PaymentDTO.CreateRequest paymentReq = new PaymentDTO.CreateRequest(
                ride.id(), BigDecimal.valueOf(35.50), PaymentMethod.CREDIT_CARD, "TXN-12345"
        );
        PaymentDTO.Response payment = paymentService.createPayment(paymentReq);
        assertNotNull(payment.id());
        assertEquals(PaymentStatus.PENDING, payment.status());

        // Update Payment to Completed
        PaymentDTO.Response completedPayment = paymentService.updatePaymentStatus(payment.id(), PaymentStatus.COMPLETED);
        assertEquals(PaymentStatus.COMPLETED, completedPayment.status());

        // Create Review (5 stars)
        ReviewDTO.CreateRequest reviewReq = new ReviewDTO.CreateRequest(
                ride.id(), 5, "Fantastic ride! Very polite driver."
        );
        ReviewDTO.Response review = reviewService.createReview(reviewReq);
        assertNotNull(review.id());
        assertEquals(5, review.rating());

        // Review by ride
        ReviewDTO.Response rideReview = reviewService.getReviewByRide(ride.id());
        assertEquals(5, rideReview.rating());
    }
}
