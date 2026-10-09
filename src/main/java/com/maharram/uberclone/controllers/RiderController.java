package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.RiderDTO;
import com.maharram.uberclone.services.RiderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @PostMapping
    public ResponseEntity<RiderDTO.Response> createRider(@Valid @RequestBody RiderDTO.CreateRequest request) {
        RiderDTO.Response created = riderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderDTO.Response> getRiderById(@PathVariable UUID id) {
        return ResponseEntity.ok(riderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RiderDTO.Response>> getAllRiders() {
        return ResponseEntity.ok(riderService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiderDTO.Response> updateRider(
            @PathVariable UUID id,
            @Valid @RequestBody RiderDTO.UpdateRequest request) {
        return ResponseEntity.ok(riderService.update(id, request));
    }

    @PatchMapping("/{id}/wallet")
    public ResponseEntity<RiderDTO.Response> updateWallet(
            @PathVariable UUID id,
            @Valid @RequestBody RiderDTO.UpdateWalletRequest request) {
        return ResponseEntity.ok(riderService.updateWalletBalance(id, request.amount()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRider(@PathVariable UUID id) {
        riderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
