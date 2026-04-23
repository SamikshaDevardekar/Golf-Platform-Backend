package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.Score;
import com.digitalheroes.golfplatform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findTop5ByUserOrderByDateAsc(User user);
    List<Score> findTop5ByUserOrderByDateDesc(User user);
    List<Score> findByUserOrderByDateAsc(User user);
    List<Score> findByUserOrderByDateDesc(User user);
}
