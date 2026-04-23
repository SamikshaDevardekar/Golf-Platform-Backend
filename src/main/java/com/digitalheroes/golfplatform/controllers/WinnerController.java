package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.PayoutStatus;
import com.digitalheroes.golfplatform.models.VerificationStatus;
import com.digitalheroes.golfplatform.models.Winner;
import com.digitalheroes.golfplatform.repositories.WinnerRepository;
import com.digitalheroes.golfplatform.services.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/winners")
public class WinnerController {
    private final WinnerRepository winnerRepository;
    private final CurrentUserService currentUserService;

    public WinnerController(WinnerRepository winnerRepository, CurrentUserService currentUserService) {
        this.winnerRepository = winnerRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/admin")
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(winnerRepository.findAll());
    }

    public record VerifyWinnerRequest(VerificationStatus verificationStatus, String proofUrl) {}

    @PostMapping("/admin/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id, @RequestBody VerifyWinnerRequest request) {
        Winner winner = winnerRepository.findById(id).orElseThrow();
        winner.setVerificationStatus(request.verificationStatus());
        if (request.proofUrl() != null && !request.proofUrl().isBlank()) {
            winner.setProofUrl(request.proofUrl());
        }
        return ResponseEntity.ok(winnerRepository.save(winner));
    }

    @PostMapping("/admin/{id}/paid")
    public ResponseEntity<?> markPaid(@PathVariable Long id) {
        Winner winner = winnerRepository.findById(id).orElseThrow();
        if (winner.getVerificationStatus() != VerificationStatus.APPROVED) {
            return ResponseEntity.badRequest().body("Winner must be approved before payout.");
        }
        winner.setPayoutStatus(PayoutStatus.PAID);
        return ResponseEntity.ok(winnerRepository.save(winner));
    }

    public record SubmitProofRequest(String proofUrl) {}

    @GetMapping("/me")
    public ResponseEntity<?> myWinners() {
        return ResponseEntity.ok(winnerRepository.findByUser(currentUserService.getRequiredUser()));
    }

    @PostMapping("/me/{id}/proof")
    public ResponseEntity<?> submitProof(@PathVariable Long id, @RequestBody SubmitProofRequest request) {
        Winner winner = winnerRepository.findById(id).orElseThrow();
        if (!winner.getUser().getId().equals(currentUserService.getRequiredUser().getId())) {
            return ResponseEntity.status(403).body("You can only upload proof for your own winnings.");
        }
        winner.setProofUrl(request.proofUrl());
        winner.setVerificationStatus(VerificationStatus.PENDING);
        return ResponseEntity.ok(winnerRepository.save(winner));
    }
}
