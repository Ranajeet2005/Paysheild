
const paymentForm = document.getElementById("payment-form");
const responseMessage = document.getElementById("response-message");
const responseJson = document.getElementById("response-json");
const lookupForm = document.getElementById("lookup-form");
const lookupResult = document.getElementById("lookup-result");

function generateKey() {
    return "ps-" + (
        crypto.randomUUID
            ? crypto.randomUUID()
            : Date.now() + "-" + Math.random().toString(16).slice(2)
    );
}

document.getElementById("idempotency-key").value = generateKey();

document.getElementById("new-key").addEventListener("click", () => {
    document.getElementById("idempotency-key").value = generateKey();
});

async function readResponse(response) {
    const text = await response.text();

    try {
        return text ? JSON.parse(text) : {};
    } catch {
        return { message: text };
    }
}

paymentForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const amount = Number(document.getElementById("amount").value);
    const currency = document.getElementById("currency").value;
    const key = document.getElementById("idempotency-key").value.trim();
    const button = document.getElementById("submit-button");

    if (!Number.isFinite(amount) || amount <= 0 || !key) {
        responseMessage.textContent = "Enter a valid amount and idempotency key.";
        return;
    }

    button.disabled = true;
    button.textContent = "Sending...";
    responseMessage.textContent = "Connecting to backend...";
    responseJson.textContent = "";

    try {
        // Confirm that these fields match CreatePaymentRequest.java.
        const payload = {
            amount: amount,
            currency: currency
        };

        const response = await fetch("/api/payments", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Idempotency-Key": key
            },
            body: JSON.stringify(payload)
        });

        const data = await readResponse(response);

        responseMessage.textContent =
            "HTTP " + response.status + (response.ok
                ? " - Request successful"
                : " - Request failed");

        responseMessage.style.color =
            response.ok ? "#65e0b3" : "#ff8f9b";

        responseJson.textContent = JSON.stringify(data, null, 2);

        const paymentId = data.id ?? data.paymentId ?? data.payment_id;

        if (paymentId !== undefined && paymentId !== null) {
            document.getElementById("payment-id").value = String(paymentId);
        }
    } catch (error) {
        responseMessage.textContent = "Could not connect to Spring Boot.";
        responseJson.textContent =
            "Check that your backend is running on port 8080.\n" + error.message;
    } finally {
        button.disabled = false;
        button.textContent = "Submit Payment";
    }
});

lookupForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("payment-id").value.trim();

    if (!id) {
        lookupResult.textContent = "Enter a payment ID.";
        return;
    }

    lookupResult.textContent = "Fetching payment...";

    try {
        const response = await fetch(
            "/api/payments/" + encodeURIComponent(id)
        );

        const data = await readResponse(response);

        lookupResult.textContent =
            "HTTP " + response.status + "\n" +
            JSON.stringify(data, null, 2);
    } catch (error) {
        lookupResult.textContent =
            "Request failed. Check your backend.\n" + error.message;
    }
});
