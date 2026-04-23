package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.Draw;
import com.digitalheroes.golfplatform.models.DrawStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    Optional<Draw> findByDrawDate(LocalDate drawDate);
    List<Draw> findByStatus(DrawStatus status);
}
