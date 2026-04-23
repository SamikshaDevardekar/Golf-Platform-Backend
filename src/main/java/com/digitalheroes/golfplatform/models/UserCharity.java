package com.digitalheroes.golfplatform.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_charity_preferences")
@Getter
@Setter
public class UserCharity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "charity_id", nullable = false)
    private Charity charity;

    private BigDecimal percentage;
}
