package com.walkmates.web;

import com.walkmates.model.Booking;
import com.walkmates.model.BookingStatus;
import com.walkmates.model.Listing;

/** Read-only presentation model shared by the seeker and provider booking views. */
public class BookingCardView {

    private final Booking booking;
    private final Listing listing;
    private final String petName;
    private final String seekerName;
    private final String providerName;

    public BookingCardView(Booking booking, Listing listing, String petName,
                           String seekerName, String providerName) {
        this.booking = booking;
        this.listing = listing;
        this.petName = petName == null ? "" : petName;
        this.seekerName = seekerName == null ? "" : seekerName;
        this.providerName = providerName == null ? "" : providerName;
    }

    public String getId() {
        return booking.getId();
    }

    public String getListingTitle() {
        return listing.getTitle();
    }

    public String getServiceType() {
        return listing.getType().name().replace('_', ' ');
    }

    public BookingStatus getStatus() {
        return booking.getStatus();
    }

    public String getStatusCssClass() {
        return booking.getStatus().name().toLowerCase().replace('_', '-');
    }

    public String getDurationLabel() {
        int minutes = booking.getDurationMinutes();
        if (minutes < 60) {
            return minutes + " min";
        }
        int hours = minutes / 60;
        int remainder = minutes % 60;
        return remainder == 0 ? hours + (hours == 1 ? " hour" : " hours")
                : hours + " h " + remainder + " min";
    }

    public double getPrice() {
        return booking.getPrice();
    }

    public String getPetName() {
        return petName;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public boolean isActionable() {
        return booking.getStatus() == BookingStatus.CONFIRMED
                || booking.getStatus() == BookingStatus.IN_PROGRESS;
    }

    public String getAdvanceLabel() {
        return booking.getStatus() == BookingStatus.CONFIRMED
                ? "Start experience"
                : "Complete experience";
    }
}
