package com.digitalheroes.golfplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "draw_numbers")
@Getter
@Setter
public class DrawNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    @JsonBackReference
    private Draw draw;

    @Column(name = "number_value", nullable = false)
    private Integer value;
}
