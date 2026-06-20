package com.walkmates.model;

import java.util.UUID;

/**
 * A Listing is a single animal-care opportunity published by a {@link Provider} — e.g. "walk my
 * dog Rex on Tuesdays" (see REQUIREMENTS FR-3).
 *
 * <p>The {@code description} is free text supplied by Providers. In Lab 3 it is the
 * <strong>prompt-injection surface</strong>: the AI explanation feature must treat it as data,
 * not instructions.</p>
 */
public class Listing {

    private final String id;
    private final String providerId;
    private String title;
    private String description;
    private ListingType type;
    private ListingStatus status;

    public Listing(String providerId, String title, String description, ListingType type) {
        this.id = UUID.randomUUID().toString();
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("Listing must belong to a provider");
        }
        this.providerId = providerId;
        setTitle(title);
        setDescription(description);
        setType(type);
        this.status = ListingStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Listing title is required");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        // Description is optional free text; null is normalised to empty.
        this.description = description == null ? "" : description;
    }

    public ListingType getType() {
        return type;
    }

    public final void setType(ListingType type) {
        if (type == null) {
            throw new IllegalArgumentException("Listing type is required");
        }
        this.type = type;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public double getBaseRatePerHour() {
        return type.getBaseRatePerHour();
    }

    /**
     * Moves the listing to a new status if the transition is legal (FR-3.2).
     *
     * @param target the desired status
     * @throws IllegalStateException if the transition is not permitted
     */
    public void transitionTo(ListingStatus target) {
        if (target == null) {
            throw new IllegalArgumentException("Target status is required");
        }
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException("Illegal listing transition: " + status + " -> " + target);
        }
        this.status = target;
    }

    public boolean isAvailable() {
        return status == ListingStatus.AVAILABLE;
    }
}
