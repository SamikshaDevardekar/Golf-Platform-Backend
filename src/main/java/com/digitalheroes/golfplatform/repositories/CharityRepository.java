package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharityRepository extends JpaRepository<Charity, Long> {
    List<Charity> findByFeaturedTrue();
}
