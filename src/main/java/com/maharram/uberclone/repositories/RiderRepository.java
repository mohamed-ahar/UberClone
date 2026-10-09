package com.maharram.uberclone.repositories;

import com.maharram.uberclone.models.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {
}
