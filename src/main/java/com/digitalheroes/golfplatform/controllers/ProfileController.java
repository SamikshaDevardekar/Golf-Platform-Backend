package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.repositories.UserRepository;
import com.digitalheroes.golfplatform.services.CurrentUserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public ProfileController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public record ProfileResponse(Long id, String name, String email, String role, boolean active) {}
    public record UpdateProfileRequest(@NotBlank String name, @Email @NotBlank String email) {}

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        User user = currentUserService.getRequiredUser();
        return ResponseEntity.ok(new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), user.isActive()));
    }

    @PutMapping("/me")
    public ResponseEntity<?> update(@RequestBody UpdateProfileRequest request) {
        User user = currentUserService.getRequiredUser();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        User saved = userRepository.save(user);
        return ResponseEntity.ok(new ProfileResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.isActive()
        ));
    }
}
