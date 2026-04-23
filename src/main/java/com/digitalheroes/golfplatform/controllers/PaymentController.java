package com.digitalheroes.golfplatform.controllers;

import com.digitalheroes.golfplatform.services.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record CheckoutRequest(String successUrl, String cancelUrl, String priceId) {}

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(@RequestBody CheckoutRequest request) throws StripeException {
        String url = paymentService.createCheckoutSession(request.successUrl(), request.cancelUrl(), request.priceId());
        return ResponseEntity.ok(Map.of("checkoutUrl", url));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String payload) {
        return ResponseEntity.ok(Map.of("received", true, "length", payload.length()));
    }
}
