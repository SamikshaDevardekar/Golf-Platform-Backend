package com.digitalheroes.golfplatform.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "independent_donations")
@Getter
@Setter
public class IndependentDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "charity_id", nullable = false)
    private Charity charity;

    @Column(nullable = false)
    private BigDecimal amount;

    private String note;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
