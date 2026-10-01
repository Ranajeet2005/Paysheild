
package com.ranajeet.paysheild.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class PaymentService {

    // Demonstration rule only; not a fraud detection model.
    private static final long REVIEW_THRESHOLD = 500_000L;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse create(
            CreatePaymentRequest request,
            String idempotencyKey) {

        String currency =
                request.currency().toUpperCase(Locale.ROOT);

        var existing =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            Payment payment = existing.get();

            boolean sameRequest =
                    payment.getAmount() == request.amount()
                    && payment.getCurrency().equals(currency)
                    && payment.getDescription()
                               .equals(request.description());

            if (!sameRequest) {
                throw new IdempotencyConflictException(
                    "This Idempotency-Key was already used " +
                    "with a different request."
                );
            }

            return PaymentResponse.from(payment);
        }

        boolean reviewRequired =
                request.amount() >= REVIEW_THRESHOLD;

        PaymentStatus status = reviewRequired
                ? PaymentStatus.REVIEW_REQUIRED
                : PaymentStatus.PENDING;

        Payment payment = new Payment(
                request.amount(),
                currency,
                request.description(),
                idempotencyKey,
                status,
                reviewRequired
        );

        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                    new PaymentNotFoundException(
                        "Payment not found: " + id
                    )
                );

        return PaymentResponse.from(payment);
    }
}

