package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.models.UserCharity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCharityRepository extends JpaRepository<UserCharity, Long> {
    Optional<UserCharity> findByUser(User user);
}
