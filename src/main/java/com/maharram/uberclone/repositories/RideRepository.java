package com.maharram.uberclone.repositories;

import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.Ride;
import com.maharram.uberclone.models.Rider;
import com.maharram.uberclone.models.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> findByRider(Rider rider);
    List<Ride> findByDriver(Driver driver);
    List<Ride> findByStatus(RideStatus status);
}
