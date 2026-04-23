package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.VerificationStatus;
import com.digitalheroes.golfplatform.repositories.CharityRepository;
import com.digitalheroes.golfplatform.repositories.DrawRepository;
import com.digitalheroes.golfplatform.repositories.IndependentDonationRepository;
import com.digitalheroes.golfplatform.repositories.SubscriptionRepository;
import com.digitalheroes.golfplatform.repositories.UserRepository;
import com.digitalheroes.golfplatform.repositories.WinnerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
public class AdminController {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final WinnerRepository winnerRepository;
    private final CharityRepository charityRepository;
    private final DrawRepository drawRepository;
    private final IndependentDonationRepository independentDonationRepository;

    public AdminController(
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            WinnerRepository winnerRepository,
            CharityRepository charityRepository,
            DrawRepository drawRepository,
            IndependentDonationRepository independentDonationRepository
    ) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.winnerRepository = winnerRepository;
        this.charityRepository = charityRepository;
        this.drawRepository = drawRepository;
        this.independentDonationRepository = independentDonationRepository;
    }

    @GetMapping
    public ResponseEntity<?> report() {
        BigDecimal totalPrizePool = winnerRepository.findAll().stream()
                .map(winner -> winner.getPrizeAmount() == null ? BigDecimal.ZERO : winner.getPrizeAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal charityContributionTotals = subscriptionRepository.findAll().stream()
                .filter(subscription -> subscription.getAmount() != null && subscription.getCharityPercentage() != null)
                .map(subscription -> subscription.getAmount()
                        .multiply(subscription.getCharityPercentage())
                        .divide(new BigDecimal("100")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal independentDonations = independentDonationRepository.findAll().stream()
                .map(donation -> donation.getAmount() == null ? BigDecimal.ZERO : donation.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingVerifications = winnerRepository.findAll().stream()
                .filter(winner -> winner.getVerificationStatus() == VerificationStatus.PENDING)
                .count();

        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "totalSubscriptions", subscriptionRepository.count(),
                "totalWinners", winnerRepository.count(),
                "totalCharities", charityRepository.count(),
                "totalPrizePool", totalPrizePool,
                "pendingVerifications", pendingVerifications,
                "charityContributionTotals", charityContributionTotals.add(independentDonations),
                "totalDraws", drawRepository.count()
        ));
    }
}
