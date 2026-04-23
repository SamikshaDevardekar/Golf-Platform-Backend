package com.digitalheroes.golfplatform.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "charities")
@Getter
@Setter
public class Charity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String imageUrl;
    private String upcomingEvents;

    private boolean featured;
}
