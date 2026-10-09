package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.PaymentDTO;
import com.maharram.uberclone.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO.Response> createPayment(@Valid @RequestBody PaymentDTO.CreateRequest request) {
        PaymentDTO.Response response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO.Response> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<PaymentDTO.Response> getPaymentByRide(@PathVariable UUID rideId) {
        return ResponseEntity.ok(paymentService.getPaymentByRide(rideId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDTO.Response> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentDTO.UpdateStatusRequest request) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
