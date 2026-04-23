package com.digitalheroes.golfplatform.services;

import com.digitalheroes.golfplatform.models.PlanType;
import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.models.SubscriptionStatus;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createOrActivate(User user, PlanType planType, Double amount, Double charityPercentage) {
        Subscription subscription = subscriptionRepository.findTopByUserOrderByStartDateDesc(user).orElse(new Subscription());
        subscription.setUser(user);
        subscription.setPlan(planType);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        if (subscription.getStartDate() == null) {
            subscription.setStartDate(LocalDate.now());
        }
        subscription.setCancelledAt(null);
        subscription.setRenewalDate(planType == PlanType.MONTHLY ? LocalDate.now().plusMonths(1) : LocalDate.now().plusYears(1));
        subscription.setAmount(BigDecimal.valueOf(amount));
        subscription.setCharityPercentage(BigDecimal.valueOf(Math.max(10.0, charityPercentage)));
        return subscriptionRepository.save(subscription);
    }

    public boolean hasActiveSubscription(User user) {
        markLapsedIfNeeded(user);
        return subscriptionRepository.findTopByUserOrderByStartDateDesc(user)
                .map(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .orElse(false);
    }

    public Optional<Subscription> getCurrent(User user) {
        markLapsedIfNeeded(user);
        return subscriptionRepository.findTopByUserOrderByStartDateDesc(user);
    }

    public Subscription cancel(User user) {
        Subscription subscription = subscriptionRepository.findTopByUserOrderByStartDateDesc(user).orElseThrow();
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setCancelledAt(LocalDate.now());
        return subscriptionRepository.save(subscription);
    }

    public Subscription reactivate(User user) {
        Subscription subscription = subscriptionRepository.findTopByUserOrderByStartDateDesc(user).orElseThrow();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setCancelledAt(null);
        subscription.setRenewalDate(subscription.getPlan() == PlanType.MONTHLY ? LocalDate.now().plusMonths(1) : LocalDate.now().plusYears(1));
        return subscriptionRepository.save(subscription);
    }

    private void markLapsedIfNeeded(User user) {
        subscriptionRepository.findTopByUserOrderByStartDateDesc(user).ifPresent(subscription -> {
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE
                    && subscription.getRenewalDate() != null
                    && subscription.getRenewalDate().isBefore(LocalDate.now())) {
                subscription.setStatus(SubscriptionStatus.LAPSED);
                subscriptionRepository.save(subscription);
            }
        });
    }
}
