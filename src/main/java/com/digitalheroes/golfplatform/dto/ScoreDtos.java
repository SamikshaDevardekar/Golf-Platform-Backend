package com.digitalheroes.golfplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ScoreDtos {
    public record AddScoreRequest(
            @NotNull @Min(1) @Max(45) Integer value,
            @NotNull LocalDate date
    ) {}
}
