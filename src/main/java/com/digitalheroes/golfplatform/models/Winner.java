package com.digitalheroes.golfplatform.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "winners")
@Getter
@Setter
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    private int matchCount;

    private BigDecimal prizeAmount;

    private String proofUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;
}
