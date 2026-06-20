package com.walkmates.service;

/**
 * External payment gateway behind an interface — a <strong>mockable seam</strong> for Lab 2.
 *
 * <p>It deliberately exposes the three paths students must test with Mockito: a successful
 * charge, a declined charge (checked {@link PaymentException}), and a timeout (unchecked
 * {@link PaymentTimeoutException} so it can model an infrastructure failure).</p>
 */
public interface PaymentService {

    /**
     * Charges the given amount to a Seeker's external payment method (used for wallet top-ups).
     *
     * @param seekerId        the paying Seeker
     * @param paymentMethodId the external payment method reference
     * @param amount          amount in SEK
     * @return a confirmation id for the charge
     * @throws PaymentException        if the charge is declined
     * @throws PaymentTimeoutException if the gateway does not respond in time
     */
    String charge(String seekerId, String paymentMethodId, double amount) throws PaymentException;

    /** Thrown when a payment is declined by the gateway. */
    class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
    }

    /** Thrown when the gateway does not respond within its timeout window. */
    class PaymentTimeoutException extends RuntimeException {
        public PaymentTimeoutException(String message) {
            super(message);
        }
    }
}
