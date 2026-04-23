package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.DrawStatus;
import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.models.UserCharity;
import com.digitalheroes.golfplatform.repositories.DrawRepository;
import com.digitalheroes.golfplatform.repositories.UserCharityRepository;
import com.digitalheroes.golfplatform.repositories.WinnerRepository;
import com.digitalheroes.golfplatform.services.CurrentUserService;
import com.digitalheroes.golfplatform.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final CurrentUserService currentUserService;
    private final SubscriptionService subscriptionService;
    private final UserCharityRepository userCharityRepository;
    private final DrawRepository drawRepository;
    private final WinnerRepository winnerRepository;

    public DashboardController(
            CurrentUserService currentUserService,
            SubscriptionService subscriptionService,
            UserCharityRepository userCharityRepository,
            DrawRepository drawRepository,
            WinnerRepository winnerRepository
    ) {
        this.currentUserService = currentUserService;
        this.subscriptionService = subscriptionService;
        this.userCharityRepository = userCharityRepository;
        this.drawRepository = drawRepository;
        this.winnerRepository = winnerRepository;
    }

    public record DashboardSummaryResponse(
            String subscriptionStatus,
            String renewalDate,
            String charityName,
            String charityPercentage,
            long drawsEntered,
            String upcomingDrawDate,
            long winningsCount
    ) {}

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        User user = currentUserService.getRequiredUser();
        Optional<Subscription> subscription = subscriptionService.getCurrent(user);
        UserCharity preference = userCharityRepository.findByUser(user).orElse(null);
        long publishedDraws = drawRepository.findByStatus(DrawStatus.PUBLISHED).size();
        long drawsEntered = subscription.isPresent() && "ACTIVE".equals(subscription.get().getStatus().name()) ? publishedDraws : 0L;
        String upcomingDraw = LocalDate.now().plusMonths(1).withDayOfMonth(1).toString();

        return ResponseEntity.ok(new DashboardSummaryResponse(
                subscription.map(s -> s.getStatus().name()).orElse("INACTIVE"),
                subscription.map(Subscription::getRenewalDate).map(LocalDate::toString).orElse(null),
                preference == null ? null : preference.getCharity().getName(),
                preference == null || preference.getPercentage() == null ? null : preference.getPercentage().toPlainString(),
                drawsEntered,
                upcomingDraw,
                winnerRepository.findByUser(user).size()
        ));
    }
}
