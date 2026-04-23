package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.Score;
import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.models.UserRole;
import com.digitalheroes.golfplatform.repositories.ScoreRepository;
import com.digitalheroes.golfplatform.repositories.SubscriptionRepository;
import com.digitalheroes.golfplatform.repositories.UserRepository;
import com.digitalheroes.golfplatform.services.ScoreService;
import com.digitalheroes.golfplatform.services.SubscriptionService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminManagementController {
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final ScoreService scoreService;

    public AdminManagementController(
            UserRepository userRepository,
            ScoreRepository scoreRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionService subscriptionService,
            ScoreService scoreService
    ) {
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.scoreService = scoreService;
    }

    public record UserSummaryResponse(Long id, String name, String email, String role, boolean active, String subscriptionStatus) {}
    public record UpdateUserRequest(@NotBlank String name, @Email @NotBlank String email, @NotNull UserRole role, @NotNull Boolean active) {}
    public record AdminScoreRequest(@NotNull Integer value, @NotNull LocalDate date) {}
    public record ScoreResponse(Long id, Integer value, LocalDate date) {}
    public record SubscriptionResponse(Long id, String plan, String status, String renewalDate) {}

    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        List<UserSummaryResponse> users = userRepository.findAll().stream()
                .map(user -> {
                    String subscriptionStatus = subscriptionRepository.findTopByUserOrderByStartDateDesc(user)
                            .map(s -> s.getStatus().name())
                            .orElse("INACTIVE");
                    return new UserSummaryResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole().name(),
                            user.isActive(),
                            subscriptionStatus
                    );
                })
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setRole(request.role());
        user.setActive(request.active());
        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserSummaryResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.isActive(),
                subscriptionRepository.findTopByUserOrderByStartDateDesc(saved).map(s -> s.getStatus().name()).orElse("INACTIVE")
        ));
    }

    @GetMapping("/users/{id}/scores")
    public ResponseEntity<?> getUserScores(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        List<ScoreResponse> scores = scoreRepository.findTop5ByUserOrderByDateDesc(user).stream()
                .map(score -> new ScoreResponse(score.getId(), score.getValue(), score.getDate()))
                .toList();
        return ResponseEntity.ok(scores);
    }

    @PutMapping("/scores/{scoreId}")
    public ResponseEntity<?> updateScore(@PathVariable Long scoreId, @RequestBody AdminScoreRequest request) {
        Score score = scoreRepository.findById(scoreId).orElseThrow();
        Score saved = scoreService.updateScoreAsAdmin(score, request.value(), request.date());
        return ResponseEntity.ok(new ScoreResponse(saved.getId(), saved.getValue(), saved.getDate()));
    }

    @DeleteMapping("/scores/{scoreId}")
    public ResponseEntity<?> deleteScore(@PathVariable Long scoreId) {
        scoreRepository.deleteById(scoreId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}/subscription")
    public ResponseEntity<?> userSubscription(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(subscriptionService.getCurrent(user)
                .map(this::toSubscriptionResponse)
                .orElse(null));
    }

    @PostMapping("/users/{id}/subscription/cancel")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toSubscriptionResponse(subscriptionService.cancel(user)));
    }

    @PostMapping("/users/{id}/subscription/reactivate")
    public ResponseEntity<?> reactivateSubscription(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toSubscriptionResponse(subscriptionService.reactivate(user)));
    }

    private SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlan() == null ? null : subscription.getPlan().name(),
                subscription.getStatus() == null ? null : subscription.getStatus().name(),
                subscription.getRenewalDate() == null ? null : subscription.getRenewalDate().toString()
        );
    }
}
