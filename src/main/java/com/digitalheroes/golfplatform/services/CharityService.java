package com.digitalheroes.golfplatform.services;

import com.digitalheroes.golfplatform.models.Charity;
import com.digitalheroes.golfplatform.models.IndependentDonation;
import com.digitalheroes.golfplatform.models.UserCharity;
import com.digitalheroes.golfplatform.repositories.CharityRepository;
import com.digitalheroes.golfplatform.repositories.IndependentDonationRepository;
import com.digitalheroes.golfplatform.repositories.UserCharityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CharityService {
    private final CharityRepository charityRepository;
    private final UserCharityRepository userCharityRepository;
    private final IndependentDonationRepository independentDonationRepository;
    private final CurrentUserService currentUserService;

    public CharityService(
            CharityRepository charityRepository,
            UserCharityRepository userCharityRepository,
            IndependentDonationRepository independentDonationRepository,
            CurrentUserService currentUserService
    ) {
        this.charityRepository = charityRepository;
        this.userCharityRepository = userCharityRepository;
        this.independentDonationRepository = independentDonationRepository;
        this.currentUserService = currentUserService;
    }

    public List<Charity> list(String search, Boolean featuredOnly) {
        List<Charity> base = Boolean.TRUE.equals(featuredOnly)
                ? charityRepository.findByFeaturedTrue()
                : charityRepository.findAll();
        if (search == null || search.isBlank()) {
            return base;
        }
        String needle = search.trim().toLowerCase();
        return base.stream()
                .filter(charity ->
                        (charity.getName() != null && charity.getName().toLowerCase().contains(needle))
                                || (charity.getDescription() != null && charity.getDescription().toLowerCase().contains(needle)))
                .toList();
    }

    public Charity create(Charity charity) {
        return charityRepository.save(charity);
    }

    public Charity update(Long id, Charity payload) {
        Charity charity = charityRepository.findById(id).orElseThrow();
        charity.setName(payload.getName());
        charity.setDescription(payload.getDescription());
        charity.setImageUrl(payload.getImageUrl());
        charity.setUpcomingEvents(payload.getUpcomingEvents());
        charity.setFeatured(payload.isFeatured());
        return charityRepository.save(charity);
    }

    public void delete(Long id) {
        charityRepository.deleteById(id);
    }

    public UserCharity setPreference(Long charityId, Double percentage) {
        Charity charity = charityRepository.findById(charityId).orElseThrow();
        UserCharity userCharity = userCharityRepository.findByUser(currentUserService.getRequiredUser()).orElse(new UserCharity());
        userCharity.setUser(currentUserService.getRequiredUser());
        userCharity.setCharity(charity);
        userCharity.setPercentage(BigDecimal.valueOf(Math.max(10.0, percentage)));
        return userCharityRepository.save(userCharity);
    }

    public UserCharity getPreference() {
        return userCharityRepository.findByUser(currentUserService.getRequiredUser()).orElse(null);
    }

    public IndependentDonation donate(Long charityId, Double amount, String note) {
        Charity charity = charityRepository.findById(charityId).orElseThrow();
        IndependentDonation donation = new IndependentDonation();
        donation.setUser(currentUserService.getRequiredUser());
        donation.setCharity(charity);
        donation.setAmount(BigDecimal.valueOf(amount == null ? 0.0 : amount));
        donation.setNote(note);
        if (donation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Donation amount must be greater than zero.");
        }
        return independentDonationRepository.save(donation);
    }
}
