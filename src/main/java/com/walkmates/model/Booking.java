package com.walkmates.model;

import java.util.UUID;

/**
 * A Booking is a Seeker's reservation of a {@link Listing} (see REQUIREMENTS FR-4).
 *
 * <p>Holds the planned duration, lifecycle status, and the price computed at confirmation. The
 * lifecycle transition logic is Lab 2 structural-testing material.</p>
 */
public class Booking {

    public static final int MIN_DURATION_MINUTES = 30;
    public static final int MAX_DURATION_MINUTES = 1_440; // 24 hours

    private final String id;
    private final String seekerId;
    private final String listingId;
    private final int durationMinutes;
    private BookingStatus status;
    private double price;

    /**
     * Creates a booking request.
     *
     * @param seekerId        the Seeker making the booking
     * @param listingId       the Listing being booked
     * @param durationMinutes planned duration, within [30, 1440] (FR-4.1)
     * @throws IllegalArgumentException if the duration is out of range
     */
    public Booking(String seekerId, String listingId, int durationMinutes) {
        if (seekerId == null || seekerId.isBlank()) {
            throw new IllegalArgumentException("Booking requires a seeker");
        }
        if (listingId == null || listingId.isBlank()) {
            throw new IllegalArgumentException("Booking requires a listing");
        }
        if (durationMinutes < MIN_DURATION_MINUTES || durationMinutes > MAX_DURATION_MINUTES) {
            throw new IllegalArgumentException(
                    "Duration must be between " + MIN_DURATION_MINUTES + " and "
                            + MAX_DURATION_MINUTES + " minutes");
        }
        this.id = UUID.randomUUID().toString();
        this.seekerId = seekerId;
        this.listingId = listingId;
        this.durationMinutes = durationMinutes;
        this.status = BookingStatus.REQUESTED;
        this.price = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getSeekerId() {
        return seekerId;
    }

    public String getListingId() {
        return listingId;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    /** Records the price computed for this booking (set at confirmation, FR-4.3). */
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    /** Whether this booking counts as active for limit checks (FR-4.2). */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Moves the booking to a new status if the transition is legal (FR-4.2).
     *
     * @param target the desired status
     * @throws IllegalStateException if the transition is not permitted
     */
    public void transitionTo(BookingStatus target) {
        if (target == null) {
            throw new IllegalArgumentException("Target status is required");
        }
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException("Illegal booking transition: " + status + " -> " + target);
        }
        this.status = target;
    }
}
