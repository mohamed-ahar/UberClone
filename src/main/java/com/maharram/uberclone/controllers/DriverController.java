package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.DriverDTO;
import com.maharram.uberclone.models.DriverStatus;
import com.maharram.uberclone.services.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverDTO.Response> createDriver(@Valid @RequestBody DriverDTO.CreateRequest request) {
        DriverDTO.Response created = driverService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO.Response> getDriverById(@PathVariable UUID id) {
        return ResponseEntity.ok(driverService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DriverDTO.Response>> getAllDrivers() {
        return ResponseEntity.ok(driverService.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverDTO.Response>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.findAvailableDrivers());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverDTO.Response>> getDriversByStatus(@PathVariable DriverStatus status) {
        return ResponseEntity.ok(driverService.findByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO.Response> updateDriver(
            @PathVariable UUID id,
            @Valid @RequestBody DriverDTO.UpdateRequest request) {
        return ResponseEntity.ok(driverService.update(id, request));
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverDTO.Response> updateLocation(
            @PathVariable UUID id,
            @Valid @RequestBody DriverDTO.UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(id, request.location()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverDTO.Response> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody DriverDTO.UpdateStatusRequest request) {
        return ResponseEntity.ok(driverService.updateStatus(id, request.status()));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<DriverDTO.Response> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody DriverDTO.UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(id, request.isAvailable()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
