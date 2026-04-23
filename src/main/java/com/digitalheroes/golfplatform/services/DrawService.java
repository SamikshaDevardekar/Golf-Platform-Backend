package com.digitalheroes.golfplatform.services;

import com.digitalheroes.golfplatform.models.Draw;
import com.digitalheroes.golfplatform.models.DrawMode;
import com.digitalheroes.golfplatform.models.DrawNumber;
import com.digitalheroes.golfplatform.models.DrawStatus;
import com.digitalheroes.golfplatform.models.Score;
import com.digitalheroes.golfplatform.models.Subscription;
import com.digitalheroes.golfplatform.models.SubscriptionStatus;
import com.digitalheroes.golfplatform.models.VerificationStatus;
import com.digitalheroes.golfplatform.models.Winner;
import com.digitalheroes.golfplatform.repositories.DrawRepository;
import com.digitalheroes.golfplatform.repositories.ScoreRepository;
import com.digitalheroes.golfplatform.repositories.SubscriptionRepository;
import com.digitalheroes.golfplatform.repositories.WinnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
public class DrawService {
    @Autowired
    private DrawRepository drawRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private WinnerRepository winnerRepository;

    public Draw generateRandomDraw(DrawMode mode) {

        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 5) {
            numbers.add(random.nextInt(45) + 1);
        }

        Draw draw = new Draw();
        draw.setDrawDate(LocalDate.now());
        draw.setMode(mode);
        draw.setStatus(DrawStatus.SIMULATED);
        List<DrawNumber> drawNumbers = numbers.stream().sorted().map(n -> {
            DrawNumber drawNumber = new DrawNumber();
            drawNumber.setDraw(draw);
            drawNumber.setValue(n);
            return drawNumber;
        }).toList();
        draw.setNumbers(drawNumbers);

        return drawRepository.save(draw);
    }

    public Draw publish(Long drawId) {
        Draw draw = drawRepository.findById(drawId).orElseThrow();
        draw.setStatus(DrawStatus.PUBLISHED);
        draw.setPublishedAt(OffsetDateTime.now());
        generateWinners(draw);
        return drawRepository.save(draw);
    }

    private void generateWinners(Draw draw) {
        winnerRepository.deleteAll(winnerRepository.findByDraw(draw));

        Set<Integer> drawValues = draw.getNumbers().stream()
                .map(DrawNumber::getValue)
                .collect(java.util.stream.Collectors.toSet());

        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
        Map<Integer, List<Winner>> winnersByMatch = new HashMap<>();
        winnersByMatch.put(3, new ArrayList<>());
        winnersByMatch.put(4, new ArrayList<>());
        winnersByMatch.put(5, new ArrayList<>());

        BigDecimal totalPool = activeSubscriptions.stream()
                .map(Subscription::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Subscription subscription : activeSubscriptions) {
            if (subscription.getUser() == null) continue;
            List<Score> scores = scoreRepository.findTop5ByUserOrderByDateAsc(subscription.getUser());
            if (scores.size() < 5) continue;

            long matchCount = scores.stream()
                    .map(Score::getValue)
                    .distinct()
                    .filter(drawValues::contains)
                    .count();

            if (matchCount >= 3) {
                Winner winner = new Winner();
                winner.setUser(subscription.getUser());
                winner.setDraw(draw);
                winner.setMatchCount((int) matchCount);
                winner.setVerificationStatus(VerificationStatus.PENDING);
                winnersByMatch.get((int) matchCount).add(winner);
            }
        }

        BigDecimal currentRollover = draw.getJackpotRollover() == null ? BigDecimal.ZERO : draw.getJackpotRollover();
        BigDecimal tier5Pool = totalPool.multiply(new BigDecimal("0.40")).add(currentRollover).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tier4Pool = totalPool.multiply(new BigDecimal("0.35")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tier3Pool = totalPool.multiply(new BigDecimal("0.25")).setScale(2, RoundingMode.HALF_UP);

        assignPrize(winnersByMatch.get(5), tier5Pool);
        assignPrize(winnersByMatch.get(4), tier4Pool);
        assignPrize(winnersByMatch.get(3), tier3Pool);

        if (winnersByMatch.get(5).isEmpty()) {
            draw.setJackpotRollover(tier5Pool);
        } else {
            draw.setJackpotRollover(BigDecimal.ZERO);
        }

        List<Winner> allWinners = new ArrayList<>();
        allWinners.addAll(winnersByMatch.get(3));
        allWinners.addAll(winnersByMatch.get(4));
        allWinners.addAll(winnersByMatch.get(5));
        winnerRepository.saveAll(allWinners);
    }

    private void assignPrize(List<Winner> winners, BigDecimal pool) {
        if (winners == null || winners.isEmpty()) return;
        BigDecimal each = pool.divide(BigDecimal.valueOf(winners.size()), 2, RoundingMode.HALF_UP);
        for (Winner winner : winners) {
            winner.setPrizeAmount(each);
        }
    }
}
