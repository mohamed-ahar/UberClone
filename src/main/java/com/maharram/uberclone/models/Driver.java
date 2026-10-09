package com.maharram.uberclone.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Driver extends User {

    private String carType;
    private String carNumber;
    private String carModel;
    private String carColor;
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DriverStatus driverStatus = DriverStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Double rating = 5.0;

    @Builder.Default
    private Integer totalRides = 0;

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ride> rides = new ArrayList<>();
}
