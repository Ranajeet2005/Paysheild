
package com.ranajeet.paysheild.payment;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        long amount,
        String currency,
        String description,
        PaymentStatus status,
        boolean reviewRequired,
        Instant createdAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                payment.getStatus(),
                payment.isReviewRequired(),
                payment.getCreatedAt()
        );
    }
}
