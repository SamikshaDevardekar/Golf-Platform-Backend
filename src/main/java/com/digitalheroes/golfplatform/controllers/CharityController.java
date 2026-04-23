package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.models.Charity;
import com.digitalheroes.golfplatform.models.UserCharity;
import com.digitalheroes.golfplatform.services.CharityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charities")
public class CharityController {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean featured
    ) {
        return ResponseEntity.ok(charityService.list(search, featured));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> create(@RequestBody Charity charity) {
        return ResponseEntity.ok(charityService.create(charity));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Charity charity) {
        return ResponseEntity.ok(charityService.update(id, charity));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        charityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record SelectCharityRequest(Long charityId, Double percentage) {}
    public record DonationRequest(Long charityId, Double amount, String note) {}
    public record CharityPreferenceResponse(Long charityId, String charityName, String percentage) {}

    @PostMapping("/select")
    public ResponseEntity<?> select(@RequestBody SelectCharityRequest request) {
        return ResponseEntity.ok(charityService.setPreference(request.charityId(), request.percentage()));
    }

    @GetMapping("/preference")
    public ResponseEntity<?> preference() {
        UserCharity preference = charityService.getPreference();
        if (preference == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(new CharityPreferenceResponse(
                preference.getCharity().getId(),
                preference.getCharity().getName(),
                preference.getPercentage() == null ? null : preference.getPercentage().toPlainString()
        ));
    }

    @PostMapping("/donate")
    public ResponseEntity<?> donate(@RequestBody DonationRequest request) {
        return ResponseEntity.ok(charityService.donate(request.charityId(), request.amount(), request.note()));
    }
}
