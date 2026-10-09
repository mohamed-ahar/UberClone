package com.maharram.uberclone.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "riders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Rider extends User {

    @Builder.Default
    private Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    private PaymentMethod preferredPaymentMethod;

    @Builder.Default
    private Double walletBalance = 0.0;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ride> rides = new ArrayList<>();
}
