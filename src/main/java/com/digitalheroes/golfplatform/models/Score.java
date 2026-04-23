package com.digitalheroes.golfplatform.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "scores")
@Getter
@Setter
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score_value", nullable = false)
    private Integer value;

    @Column(name = "score_date", nullable = false)
    private LocalDate date;
}
