package com.walkmates.service;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import org.springframework.stereotype.Service;

/**
 * Registration and wallet operations for {@link Seeker}s.
 *
 * <p>{@link #topUp} is a second {@link PaymentService} mock-seam example for Lab 2: it charges
 * the external gateway first and only then credits the wallet, so the decline/timeout paths are
 * meaningful (the wallet must not be credited if the charge fails).</p>
 */
@Service
public class SeekerService {

    private final SeekerRepository seekers;
    private final PaymentService payments;
    private final NotificationService notifications;

    public SeekerService(SeekerRepository seekers,
                         PaymentService payments,
                         NotificationService notifications) {
        this.seekers = seekers;
        this.payments = payments;
        this.notifications = notifications;
    }

    /** Registers a new Seeker (validation happens in the {@link Seeker} constructor). */
    public Seeker register(String email, String displayName, String phoneNumber) {
        if (seekers.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        return seekers.save(new Seeker(email, displayName, phoneNumber));
    }

    /**
     * Tops up a Seeker's wallet: charges the external gateway, then credits the wallet only if
     * the charge succeeds.
     *
     * @return the updated Seeker
     * @throws PaymentService.PaymentException if the gateway declines (wallet left unchanged)
     */
    public Seeker topUp(String seekerId, String paymentMethodId, double amount)
            throws PaymentService.PaymentException {
        Seeker seeker = seekers.findById(seekerId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown seeker: " + seekerId));
        // Charge first; if this throws (decline) or times out, the wallet is never credited.
        payments.charge(seekerId, paymentMethodId, amount);
        seeker.addFunds(amount);
        return seekers.save(seeker);
    }
}
