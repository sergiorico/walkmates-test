package com.walkmates.model;

import java.util.Set;

/**
 * Lifecycle status of a {@link Listing} (see REQUIREMENTS FR-3.2).
 *
 * <p>The legal transitions are encoded in {@link #canTransitionTo(ListingStatus)}. This switch
 * is a primary Lab 2 target: it has enough branches that statement vs. branch coverage differ,
 * and it is where the state-machine mutation/structural work happens.</p>
 */
public enum ListingStatus {

    AVAILABLE,
    BOOKED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    /**
     * Returns whether moving from this status to {@code target} is a legal transition per
     * FR-3.2.
     *
     * @param target the status we want to move to
     * @return true if the transition is allowed
     */
    public boolean canTransitionTo(ListingStatus target) {
        return switch (this) {
            case AVAILABLE -> target == BOOKED;
            case BOOKED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == COMPLETED || target == CANCELLED;
            case COMPLETED -> target == AVAILABLE;
            case CANCELLED -> target == AVAILABLE;
        };
    }

    /** Convenience set of the statuses from which a slot can be re-opened to AVAILABLE. */
    public static Set<ListingStatus> reopenableFrom() {
        return Set.of(COMPLETED, CANCELLED);
    }
}
