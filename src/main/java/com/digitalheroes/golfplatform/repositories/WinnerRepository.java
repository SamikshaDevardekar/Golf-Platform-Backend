package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.Draw;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.models.Winner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    List<Winner> findByDraw(Draw draw);
    List<Winner> findByUser(User user);
}
