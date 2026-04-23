package com.digitalheroes.golfplatform.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "draws")
@Getter
@Setter
public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "draw_month", nullable = false)
    private LocalDate drawDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrawMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrawStatus status = DrawStatus.DRAFT;

    private OffsetDateTime publishedAt;

    private BigDecimal jackpotRollover = BigDecimal.ZERO;

    @OneToMany(mappedBy = "draw", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DrawNumber> numbers = new ArrayList<>();
}
