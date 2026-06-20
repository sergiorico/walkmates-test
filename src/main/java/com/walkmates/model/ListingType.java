package com.walkmates.model;

/**
 * Kind of animal-care opportunity a {@link Listing} represents, with its fixed base rate per
 * hour in SEK (see REQUIREMENTS FR-3.1).
 *
 * <p>{@code SHELTER_VOLUNTEER} is free (rate 0) but still produces real bookings that count
 * against limits — a deliberate edge case for boundary/decision testing.</p>
 */
public enum ListingType {

    DOG_WALK(80.0),
    DAY_VISIT(100.0),
    PET_SITTING(120.0),
    HOUSE_SITTING(150.0),
    SHELTER_VOLUNTEER(0.0);

    private final double baseRatePerHour;

    ListingType(double baseRatePerHour) {
        this.baseRatePerHour = baseRatePerHour;
    }

    /** Base price per hour in SEK for this listing type (FR-3.1). */
    public double getBaseRatePerHour() {
        return baseRatePerHour;
    }
}
