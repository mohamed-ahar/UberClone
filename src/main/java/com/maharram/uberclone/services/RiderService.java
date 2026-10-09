package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.RiderDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.Rider;
import com.maharram.uberclone.repositories.RiderRepository;
import com.maharram.uberclone.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RiderService {

    private final RiderRepository riderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RiderService(RiderRepository riderRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.riderRepository = riderRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    @Transactional
    public RiderDTO.Response create(RiderDTO.CreateRequest request) {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        String encodedPassword = this.passwordEncoder.encode(request.password());
        Rider rider = Rider.builder()
                .firstName(request.firstname())
                .lastName(request.lastname())
                .age(request.age())
                .email(request.email())
                .password(encodedPassword)
                .phoneNumber(request.phoneNumber())
                .role("USER_ROLE")
                .rating(5.0)
                .preferredPaymentMethod(request.preferredPaymentMethod())
                .walletBalance(request.walletBalance() != null ? request.walletBalance() : 0.0)
                .build();

        Rider saved = this.riderRepository.save(rider);
        return RiderDTO.Response.fromEntity(saved);
    }

    // READ by ID
    @Transactional(readOnly = true)
    public RiderDTO.Response findById(UUID id) {
        Rider rider = this.riderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + id));
        return RiderDTO.Response.fromEntity(rider);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<RiderDTO.Response> findAll() {
        return this.riderRepository.findAll().stream()
                .map(RiderDTO.Response::fromEntity)
                .toList();
    }

    // UPDATE
    @Transactional
    public RiderDTO.Response update(UUID id, RiderDTO.UpdateRequest request) {
        Rider rider = this.riderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + id));

        if (request.firstname() != null && !request.firstname().isBlank()) {
            rider.setFirstName(request.firstname());
        }
        if (request.lastname() != null && !request.lastname().isBlank()) {
            rider.setLastName(request.lastname());
        }
        if (request.age() != null) {
            rider.setAge(request.age());
        }
        if (request.phoneNumber() != null) {
            rider.setPhoneNumber(request.phoneNumber());
        }
        if (request.preferredPaymentMethod() != null) {
            rider.setPreferredPaymentMethod(request.preferredPaymentMethod());
        }
        if (request.walletBalance() != null) {
            rider.setWalletBalance(request.walletBalance());
        }

        Rider updated = this.riderRepository.save(rider);
        return RiderDTO.Response.fromEntity(updated);
    }

    // UPDATE Wallet Balance
    @Transactional
    public RiderDTO.Response updateWalletBalance(UUID riderId, Double deltaAmount) {
        Rider rider = this.riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + riderId));
        double currentBalance = rider.getWalletBalance() != null ? rider.getWalletBalance() : 0.0;
        double newBalance = currentBalance + deltaAmount;
        if (newBalance < 0.0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }
        rider.setWalletBalance(newBalance);
        Rider updated = this.riderRepository.save(rider);
        return RiderDTO.Response.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void delete(UUID id) {
        if (!this.riderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rider not found with id: " + id);
        }
        this.riderRepository.deleteById(id);
    }
}
