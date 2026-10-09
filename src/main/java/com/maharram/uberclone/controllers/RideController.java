package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.RideDTO;
import com.maharram.uberclone.models.RideStatus;
import com.maharram.uberclone.services.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideDTO.Response> createRide(@Valid @RequestBody RideDTO.CreateRequest request) {
        RideDTO.Response response = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO.Response> getRideById(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideDTO.Response>> getRidesByRider(@PathVariable UUID riderId) {
        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideDTO.Response>> getRidesByDriver(@PathVariable UUID driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RideDTO.Response>> getRidesByStatus(@PathVariable RideStatus status) {
        return ResponseEntity.ok(rideService.getRidesByStatus(status));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<RideDTO.Response> assignDriver(
            @PathVariable UUID id,
            @Valid @RequestBody RideDTO.AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(id, request.driverId()));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<RideDTO.Response> startRide(
            @PathVariable UUID id,
            @RequestBody RideDTO.StartRideRequest request) {
        String otp = request != null ? request.otp() : null;
        return ResponseEntity.ok(rideService.startRide(id, otp));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RideDTO.Response> completeRide(
            @PathVariable UUID id,
            @RequestBody RideDTO.CompleteRequest request) {
        return ResponseEntity.ok(rideService.completeRide(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RideDTO.Response> cancelRide(
            @PathVariable UUID id,
            @RequestBody RideDTO.CancelRideRequest request) {
        String reason = request != null ? request.reason() : "Cancelled by user";
        return ResponseEntity.ok(rideService.cancelRide(id, reason));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable UUID id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}
