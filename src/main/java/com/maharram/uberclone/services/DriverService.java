package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.DriverDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.DriverStatus;
import com.maharram.uberclone.models.Location;
import com.maharram.uberclone.repositories.DriverRepository;
import com.maharram.uberclone.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    @Transactional
    public DriverDTO.Response create(DriverDTO.CreateRequest request) {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        String encodedPassword = this.passwordEncoder.encode(request.password());
        Driver driver = Driver.builder()
                .firstName(request.firstname())
                .lastName(request.lastname())
                .age(request.age())
                .email(request.email())
                .password(encodedPassword)
                .phoneNumber(request.phoneNumber())
                .role("DRIVER_ROLE")
                .carType(request.carType())
                .carNumber(request.carNumber())
                .carModel(request.carModel())
                .carColor(request.carColor())
                .licenseNumber(request.licenseNumber())
                .vehicleType(request.vehicleType())
                .location(request.location())
                .driverStatus(DriverStatus.AVAILABLE)
                .isAvailable(true)
                .rating(5.0)
                .totalRides(0)
                .build();

        Driver saved = this.driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(saved);
    }

    // READ by ID
    @Transactional(readOnly = true)
    public DriverDTO.Response findById(UUID id) {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverDTO.Response.fromEntity(driver);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<DriverDTO.Response> findAll() {
        return this.driverRepository.findAll().stream()
                .map(DriverDTO.Response::fromEntity)
                .toList();
    }

    // READ Available Drivers
    @Transactional(readOnly = true)
    public List<DriverDTO.Response> findAvailableDrivers() {
        return this.driverRepository.findByIsAvailableTrue().stream()
                .map(DriverDTO.Response::fromEntity)
                .toList();
    }

    // READ By Status
    @Transactional(readOnly = true)
    public List<DriverDTO.Response> findByStatus(DriverStatus status) {
        return this.driverRepository.findByDriverStatus(status).stream()
                .map(DriverDTO.Response::fromEntity)
                .toList();
    }

    // UPDATE General Info
    @Transactional
    public DriverDTO.Response update(UUID id, DriverDTO.UpdateRequest request) {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (request.firstname() != null && !request.firstname().isBlank()) {
            driver.setFirstName(request.firstname());
        }
        if (request.lastname() != null && !request.lastname().isBlank()) {
            driver.setLastName(request.lastname());
        }
        if (request.age() != null) {
            driver.setAge(request.age());
        }
        if (request.phoneNumber() != null) {
            driver.setPhoneNumber(request.phoneNumber());
        }
        if (request.carType() != null) {
            driver.setCarType(request.carType());
        }
        if (request.carNumber() != null) {
            driver.setCarNumber(request.carNumber());
        }
        if (request.carModel() != null) {
            driver.setCarModel(request.carModel());
        }
        if (request.carColor() != null) {
            driver.setCarColor(request.carColor());
        }
        if (request.licenseNumber() != null) {
            driver.setLicenseNumber(request.licenseNumber());
        }
        if (request.vehicleType() != null) {
            driver.setVehicleType(request.vehicleType());
        }
        if (request.isAvailable() != null) {
            driver.setIsAvailable(request.isAvailable());
        }
        if (request.driverStatus() != null) {
            driver.setDriverStatus(request.driverStatus());
        }
        if (request.location() != null) {
            driver.setLocation(request.location());
        }

        Driver updated = this.driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(updated);
    }

    // UPDATE Location
    @Transactional
    public DriverDTO.Response updateLocation(UUID driverId, Location location) {
        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        driver.setLocation(location);
        Driver updated = this.driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(updated);
    }

    // UPDATE Status
    @Transactional
    public DriverDTO.Response updateStatus(UUID driverId, DriverStatus status) {
        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        driver.setDriverStatus(status);
        if (status == DriverStatus.AVAILABLE) {
            driver.setIsAvailable(true);
        } else if (status == DriverStatus.BUSY || status == DriverStatus.OFFLINE) {
            driver.setIsAvailable(false);
        }
        Driver updated = this.driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(updated);
    }

    // UPDATE Availability
    @Transactional
    public DriverDTO.Response updateAvailability(UUID driverId, Boolean isAvailable) {
        Driver driver = this.driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        driver.setIsAvailable(isAvailable);
        if (Boolean.FALSE.equals(isAvailable)) {
            driver.setDriverStatus(DriverStatus.OFFLINE);
        } else {
            driver.setDriverStatus(DriverStatus.AVAILABLE);
        }
        Driver updated = this.driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void delete(UUID id) {
        if (!this.driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }
        this.driverRepository.deleteById(id);
    }
}
