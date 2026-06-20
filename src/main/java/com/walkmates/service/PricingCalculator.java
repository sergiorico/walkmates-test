package com.walkmates.service;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.Seeker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Computes the price of a {@link Booking} per REQUIREMENTS FR-4.3.
 *
 * <p>This is the primary <strong>Lab 2 structural + mutation target</strong>. The method has
 * several decision points (free listings, the overnight surcharge boundary, the per-tier fee)
 * so statement and branch coverage diverge, and the boundary comparisons are exactly the kind
 * of thing PIT's boundary mutators probe.</p>
 */
@Service
public class PricingCalculator {

    private static final double OVERNIGHT_THRESHOLD_MINUTES = 480; // 8 hours
    private static final double OVERNIGHT_SURCHARGE_RATE = 0.20;

    /**
     * Calculates the total price for a booking.
     *
     * @param booking the booking (duration in minutes)
     * @param listing the listing being booked (provides the base rate)
     * @param seeker  the seeker (provides the trust-tier platform fee)
     * @return the total price in SEK, rounded to 2 decimal places
     */
    public double priceFor(Booking booking, Listing listing, Seeker seeker) {
        if (booking == null || listing == null || seeker == null) {
            throw new IllegalArgumentException("Booking, listing and seeker are all required");
        }

        double baseRate = listing.getBaseRatePerHour();
        // Free listings (e.g. SHELTER_VOLUNTEER) short-circuit to zero (FR-3.1 / FR-4.3).
        if (baseRate == 0.0) {
            return 0.00;
        }

        double hours = booking.getDurationMinutes() / 60.0;
        double baseCost = hours * baseRate;

        double overnightExtra = 0.0;
        // Long bookings carry an overnight surcharge (FR-4.3).
        if (booking.getDurationMinutes() >= OVERNIGHT_THRESHOLD_MINUTES) {
            overnightExtra = baseCost * OVERNIGHT_SURCHARGE_RATE;
        }

        double subtotal = baseCost + overnightExtra;
        double fee = subtotal * seeker.getTrustTier().getPlatformFee();
        return round2(subtotal + fee);
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
