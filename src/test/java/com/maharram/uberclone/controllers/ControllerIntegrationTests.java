package com.maharram.uberclone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maharram.uberclone.dtos.*;
import com.maharram.uberclone.models.*;
import com.maharram.uberclone.services.DriverService;
import com.maharram.uberclone.services.RiderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RiderService riderService;

    @Test
    void testPublicDriverAndRiderRegistration() throws Exception {
        // Public Driver Registration
        DriverDTO.CreateRequest driverReq = new DriverDTO.CreateRequest(
                "Dan", "Driver", 33, "dan." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "9998887777", "Sedan", "DAN-001", "Honda Accord", "Blue", "LIC-9988",
                VehicleType.UBER_X, new Location(40.71, -74.00, "Broadway", "NYC")
        );

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Dan"))
                .andExpect(jsonPath("$.carNumber").value("DAN-001"));

        // Public Rider Registration
        RiderDTO.CreateRequest riderReq = new RiderDTO.CreateRequest(
                "Rachel", "Rider", 26, "rachel." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "1114447777", PaymentMethod.CREDIT_CARD, 80.0
        );

        mockMvc.perform(post("/api/riders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(riderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Rachel"))
                .andExpect(jsonPath("$.walletBalance").value(80.0));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ADMIN_ROLE", "USER_ROLE"})
    void testAuthenticatedUserEndpoints() throws Exception {
        RiderDTO.Response rider = riderService.create(new RiderDTO.CreateRequest(
                "UserA", "LastA", 29, "usera." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "1231231234", PaymentMethod.CASH, 20.0
        ));

        // Get User by ID
        mockMvc.perform(get("/api/users/" + rider.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("UserA"));

        // Update User
        UserDTO.UpdateRequest updateReq = new UserDTO.UpdateRequest(
                "UserA_Updated", "LastA", 30, "9990001111", UserDTO.UserRole.USER_ROLE
        );
        mockMvc.perform(put("/api/users/" + rider.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("UserA_Updated"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = {"USER_ROLE"})
    void testRideWorkflowThroughControllers() throws Exception {
        // Pre-create rider and driver
        RiderDTO.Response rider = riderService.create(new RiderDTO.CreateRequest(
                "RideRider", "Test", 25, "rider." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "3332221111", PaymentMethod.CREDIT_CARD, 100.0
        ));
        DriverDTO.Response driver = driverService.create(new DriverDTO.CreateRequest(
                "RideDriver", "Test", 35, "driver." + UUID.randomUUID() + "@test.com", "SecurePass123!",
                "4443332222", "Sedan", "DRV-123", "Toyota Camry", "White", "LIC-1122",
                VehicleType.UBER_X, new Location(40.71, -74.00, "SoHo", "NYC")
        ));

        // 1. Request Ride
        RideDTO.CreateRequest rideReq = new RideDTO.CreateRequest(
                rider.id(),
                new Location(40.71, -74.00, "SoHo", "NYC"),
                new Location(40.75, -73.98, "Midtown", "NYC"),
                PaymentMethod.CREDIT_CARD
        );

        String rideResponseJson = mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andReturn().getResponse().getContentAsString();

        RideDTO.Response ride = objectMapper.readValue(rideResponseJson, RideDTO.Response.class);

        // 2. Assign Driver
        mockMvc.perform(post("/api/rides/" + ride.id() + "/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RideDTO.AssignDriverRequest(driver.id()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.driverId").value(driver.id().toString()));

        // 3. Start Ride
        mockMvc.perform(post("/api/rides/" + ride.id() + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RideDTO.StartRideRequest(ride.otp()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 4. Complete Ride
        mockMvc.perform(post("/api/rides/" + ride.id() + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RideDTO.CompleteRequest(BigDecimal.valueOf(42.00), 10.5, 25))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.fare").value(42.00));

        // 5. Create Payment
        PaymentDTO.CreateRequest payReq = new PaymentDTO.CreateRequest(
                ride.id(), BigDecimal.valueOf(42.00), PaymentMethod.CREDIT_CARD, "TXN-998877"
        );
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // 6. Submit Review
        ReviewDTO.CreateRequest revReq = new ReviewDTO.CreateRequest(
                ride.id(), 5, "Amazing driver!"
        );
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }
}
