package com.maharram.uberclone.repositories;

import com.maharram.uberclone.models.Driver;
import com.maharram.uberclone.models.Review;
import com.maharram.uberclone.models.Ride;
import com.maharram.uberclone.models.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByRide(Ride ride);
    List<Review> findByDriver(Driver driver);
    List<Review> findByRider(Rider rider);
}
