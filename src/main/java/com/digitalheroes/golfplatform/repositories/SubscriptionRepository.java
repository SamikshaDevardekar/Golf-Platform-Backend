package com.digitalheroes.golfplatform.repositories;

import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.models.SubscriptionStatus;
import com.digitalheroes.golfplatform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserOrderByStartDateDesc(User user);
    List<Subscription> findByStatus(SubscriptionStatus status);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
