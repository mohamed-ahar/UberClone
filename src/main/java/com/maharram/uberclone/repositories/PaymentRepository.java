package com.maharram.uberclone.repositories;

import com.maharram.uberclone.models.Payment;
import com.maharram.uberclone.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByRide(Ride ride);
}
