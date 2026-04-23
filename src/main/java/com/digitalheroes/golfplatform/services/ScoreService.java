package com.digitalheroes.golfplatform.services;

import com.digitalheroes.golfplatform.models.Score;
import com.digitalheroes.golfplatform.models.User;
import com.digitalheroes.golfplatform.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public void addScore(User user, Integer value, LocalDate date) {
        validateScore(value);
        Score score = new Score();
        score.setUser(user);
        score.setValue(value);
        score.setDate(date);
        scoreRepository.save(score);
        enforceLatestFive(user);
    }

    public List<Score> getScores(User user) {
        return scoreRepository.findTop5ByUserOrderByDateDesc(user);
    }

    public Score updateScore(User user, Long scoreId, Integer value, LocalDate date) {
        validateScore(value);
        Score score = scoreRepository.findById(scoreId).orElseThrow();
        if (!score.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own scores");
        }
        score.setValue(value);
        score.setDate(date);
        Score saved = scoreRepository.save(score);
        enforceLatestFive(user);
        return saved;
    }

    public void deleteScore(User user, Long scoreId) {
        Score score = scoreRepository.findById(scoreId).orElseThrow();
        if (!score.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own scores");
        }
        scoreRepository.delete(score);
    }

    public Score updateScoreAsAdmin(Score score, Integer value, LocalDate date) {
        validateScore(value);
        score.setValue(value);
        score.setDate(date);
        Score saved = scoreRepository.save(score);
        enforceLatestFive(score.getUser());
        return saved;
    }

    private void validateScore(Integer value) {
        if (value == null || value < 1 || value > 45) {
            throw new RuntimeException("Invalid score");
        }
    }

    private void enforceLatestFive(User user) {
        List<Score> scoresAsc = scoreRepository.findByUserOrderByDateAsc(user);
        while (scoresAsc.size() > 5) {
            scoreRepository.delete(scoresAsc.get(0));
            scoresAsc.remove(0);
        }
    }
}
