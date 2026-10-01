
package com.ranajeet.paysheild.payment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private UUID id;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 120)
    private String description;

    @Column(name = "idempotency_key",
            nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(nullable = false)
    private boolean reviewRequired;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Payment() {
        // Required by JPA
    }

    public Payment(
            long amount,
            String currency,
            String description,
            String idempotencyKey,
            PaymentStatus status,
            boolean reviewRequired) {

        this.id = UUID.randomUUID();
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.reviewRequired = reviewRequired;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public boolean isReviewRequired() {
        return reviewRequired;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
