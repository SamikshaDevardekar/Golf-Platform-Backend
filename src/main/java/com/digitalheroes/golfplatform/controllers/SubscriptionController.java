package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.PlanType;
import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.services.CurrentUserService;
import com.digitalheroes.golfplatform.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final CurrentUserService currentUserService;

    public SubscriptionController(SubscriptionService subscriptionService, CurrentUserService currentUserService) {
        this.subscriptionService = subscriptionService;
        this.currentUserService = currentUserService;
    }

    public record CreateSubscriptionRequest(PlanType planType, Double amount, Double charityPercentage) {}
    public record SubscriptionResponse(
            Long id,
            String plan,
            String status,
            String startDate,
            String renewalDate,
            String cancelledAt,
            String amount,
            String charityPercentage
    ) {}

    private SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlan() == null ? null : subscription.getPlan().name(),
                subscription.getStatus() == null ? null : subscription.getStatus().name(),
                subscription.getStartDate() == null ? null : subscription.getStartDate().toString(),
                subscription.getRenewalDate() == null ? null : subscription.getRenewalDate().toString(),
                subscription.getCancelledAt() == null ? null : subscription.getCancelledAt().toString(),
                subscription.getAmount() == null ? null : subscription.getAmount().toPlainString(),
                subscription.getCharityPercentage() == null ? null : subscription.getCharityPercentage().toPlainString()
        );
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestBody CreateSubscriptionRequest request) {
        Subscription subscription = subscriptionService.createOrActivate(
                currentUserService.getRequiredUser(),
                request.planType(),
                request.amount(),
                request.charityPercentage()
        );
        return ResponseEntity.ok(toResponse(subscription));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(subscriptionService.hasActiveSubscription(currentUserService.getRequiredUser()));
    }

    @GetMapping("/current")
    public ResponseEntity<?> current() {
        return ResponseEntity.ok(subscriptionService.getCurrent(currentUserService.getRequiredUser()).map(this::toResponse).orElse(null));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(toResponse(subscriptionService.cancel(currentUserService.getRequiredUser())));
    }

    @PostMapping("/reactivate")
    public ResponseEntity<?> reactivate() {
        return ResponseEntity.ok(toResponse(subscriptionService.reactivate(currentUserService.getRequiredUser())));
    }
}
