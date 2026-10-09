package com.maharram.uberclone.repositories;

import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByDriverStatus(DriverStatus status);
    List<Driver> findByIsAvailableTrue();
}
