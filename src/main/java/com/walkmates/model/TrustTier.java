package com.walkmates.model;

/**
 * Trust tier of a {@link Seeker}. Each tier fixes two numeric limits (see REQUIREMENTS FR-1.2):
 * the maximum number of concurrent active bookings, and the platform service fee applied to
 * every booking.
 *
 * <p>These values are the source material for the Lab 1 decision table and the Lab 2 boundary
 * (off-by-one) mutation work, so they are intentionally simple and exact.</p>
 */
public enum TrustTier {

    NEW(1, 0.15),
    VERIFIED(3, 0.12),
    TRUSTED(5, 0.08),
    PRO_SITTER(10, 0.05);

    private final int maxConcurrentBookings;
    private final double platformFee;

    TrustTier(int maxConcurrentBookings, double platformFee) {
        this.maxConcurrentBookings = maxConcurrentBookings;
        this.platformFee = platformFee;
    }

    /** Maximum number of active bookings a Seeker in this tier may hold at once (FR-1.2). */
    public int getMaxConcurrentBookings() {
        return maxConcurrentBookings;
    }

    /** Platform fee fraction applied to each booking subtotal, e.g. 0.15 = 15% (FR-1.2). */
    public double getPlatformFee() {
        return platformFee;
    }
}
