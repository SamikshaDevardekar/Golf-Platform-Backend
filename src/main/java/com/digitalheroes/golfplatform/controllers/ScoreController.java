package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.dto.ScoreDtos;
import com.digitalheroes.golfplatform.services.ScoreService;
import com.digitalheroes.golfplatform.services.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    private final ScoreService scoreService;
    private final CurrentUserService currentUserService;

    public ScoreController(ScoreService scoreService, CurrentUserService currentUserService) {
        this.scoreService = scoreService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<?> addScore(@Valid @RequestBody ScoreDtos.AddScoreRequest request) {
        scoreService.addScore(currentUserService.getRequiredUser(), request.value(), request.date());
        return ResponseEntity.ok("Score added");
    }

    @GetMapping
    public ResponseEntity<?> listScores() {
        return ResponseEntity.ok(scoreService.getScores(currentUserService.getRequiredUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateScore(@PathVariable Long id, @Valid @RequestBody ScoreDtos.AddScoreRequest request) {
        return ResponseEntity.ok(scoreService.updateScore(currentUserService.getRequiredUser(), id, request.value(), request.date()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScore(@PathVariable Long id) {
        scoreService.deleteScore(currentUserService.getRequiredUser(), id);
        return ResponseEntity.noContent().build();
    }
}
