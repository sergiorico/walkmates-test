package com.walkmates.model;

/**
 * Lifecycle status of a {@link Booking} (see REQUIREMENTS FR-4.2).
 *
 * <p>{@code REQUESTED}, {@code CONFIRMED} and {@code IN_PROGRESS} are <em>active</em> — they
 * count against the Seeker's trust-tier limit and the Provider's capacity.</p>
 */
public enum BookingStatus {

    REQUESTED(true),
    CONFIRMED(true),
    IN_PROGRESS(true),
    COMPLETED(false),
    CANCELLED(false);

    private final boolean active;

    BookingStatus(boolean active) {
        this.active = active;
    }

    /** Whether a booking in this status counts as "active" for limit checks (FR-4.2). */
    public boolean isActive() {
        return active;
    }

    /**
     * Legal lifecycle transitions per FR-4.2:
     * REQUESTED -> CONFIRMED -> IN_PROGRESS -> COMPLETED, plus REQUESTED/CONFIRMED -> CANCELLED.
     *
     * @param target the status we want to move to
     * @return true if the transition is allowed
     */
    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case REQUESTED -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
