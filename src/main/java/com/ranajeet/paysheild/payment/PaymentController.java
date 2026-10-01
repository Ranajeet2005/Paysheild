
package com.ranajeet.paysheild.payment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        if (idempotencyKey.isBlank()
                || idempotencyKey.length() > 100) {
            throw new InvalidRequestException(
                "Idempotency-Key must contain 1 to 100 characters."
            );
        }

        return paymentService.create(request, idempotencyKey);
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable UUID id) {
        return paymentService.getById(id);
    }
}
