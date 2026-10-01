
package com.ranajeet.paysheild.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest(

        @Min(value = 1, message = "Amount must be greater than zero")
        long amount,

        @NotBlank(message = "Currency is required")
        @Pattern(
            regexp = "[A-Za-z]{3}",
            message = "Currency must be a 3-letter code"
        )
        String currency,

        @NotBlank(message = "Description is required")
        @Size(
            max = 120,
            message = "Description must be at most 120 characters"
        )
        String description

) {
}
