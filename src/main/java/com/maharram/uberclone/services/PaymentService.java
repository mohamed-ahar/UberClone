package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.PaymentDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.Payment;
import com.maharram.uberclone.models.PaymentStatus;
import com.maharram.uberclone.models.Ride;
import com.maharram.uberclone.repositories.PaymentRepository;
import com.maharram.uberclone.repositories.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    public PaymentService(PaymentRepository paymentRepository, RideRepository rideRepository) {
        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
    }

    // CREATE
    @Transactional
    public PaymentDTO.Response createPayment(PaymentDTO.CreateRequest request) {
        Ride ride = this.rideRepository.findById(request.rideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + request.rideId()));

        String txnId = request.transactionId() != null ? request.transactionId() : "TXN-" + UUID.randomUUID();

        Payment payment = Payment.builder()
                .ride(ride)
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(txnId)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment saved = this.paymentRepository.save(payment);

        // Also update ride payment status
        ride.setPaymentStatus(PaymentStatus.PENDING);
        this.rideRepository.save(ride);

        return PaymentDTO.Response.fromEntity(saved);
    }

    // READ by ID
    @Transactional(readOnly = true)
    public PaymentDTO.Response getPaymentById(UUID id) {
        Payment payment = this.paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentDTO.Response.fromEntity(payment);
    }

    // READ by Ride
    @Transactional(readOnly = true)
    public PaymentDTO.Response getPaymentByRide(UUID rideId) {
        Ride ride = this.rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        Payment payment = this.paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ride id: " + rideId));
        return PaymentDTO.Response.fromEntity(payment);
    }

    // UPDATE Status
    @Transactional
    public PaymentDTO.Response updatePaymentStatus(UUID id, PaymentStatus status) {
        Payment payment = this.paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        payment.setStatus(status);
        Payment updated = this.paymentRepository.save(payment);

        // Reflect in Ride as well
        if (payment.getRide() != null) {
            payment.getRide().setPaymentStatus(status);
            this.rideRepository.save(payment.getRide());
        }

        return PaymentDTO.Response.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void deletePayment(UUID id) {
        if (!this.paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        this.paymentRepository.deleteById(id);
    }
}
