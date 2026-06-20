package com.walkmates.service;

import com.walkmates.model.Booking;
import com.walkmates.model.BookingStatus;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingStatus;
import com.walkmates.model.Provider;
import com.walkmates.model.Seeker;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import org.springframework.stereotype.Service;

/**
 * Orchestrates booking creation and lifecycle per REQUIREMENTS FR-4.
 *
 * <p>This service is the Lab 2 component-isolation target: it depends on four repositories, the
 * {@link PricingCalculator}, and the {@link NotificationService} seam, so students mock those to
 * test it in isolation (including the notification interaction and the failure paths).</p>
 */
@Service
public class BookingService {

    private final SeekerRepository seekers;
    private final ListingRepository listings;
    private final ProviderRepository providers;
    private final BookingRepository bookings;
    private final PricingCalculator pricing;
    private final NotificationService notifications;

    public BookingService(SeekerRepository seekers,
                          ListingRepository listings,
                          ProviderRepository providers,
                          BookingRepository bookings,
                          PricingCalculator pricing,
                          NotificationService notifications) {
        this.seekers = seekers;
        this.listings = listings;
        this.providers = providers;
        this.bookings = bookings;
        this.pricing = pricing;
        this.notifications = notifications;
    }

    /**
     * Creates and confirms a booking if all FR-4.4 conditions hold, charging the Seeker's wallet
     * and notifying them. Throws {@link BookingRejectedException} with the first failing reason.
     *
     * @param seekerId        the booking Seeker
     * @param listingId       the Listing to book
     * @param durationMinutes planned duration in minutes (FR-4.1)
     * @return the confirmed booking
     */
    public Booking createBooking(String seekerId, String listingId, int durationMinutes) {
        Seeker seeker = seekers.findById(seekerId)
                .orElseThrow(() -> new BookingRejectedException("Unknown seeker: " + seekerId));
        Listing listing = listings.findById(listingId)
                .orElseThrow(() -> new BookingRejectedException("Unknown listing: " + listingId));

        // Rule 1: listing must be available.
        if (!listing.isAvailable()) {
            throw new BookingRejectedException("Listing is not available");
        }

        // Rule 4: duration within range — constructed here so the range check (FR-4.1) runs.
        Booking booking = new Booking(seekerId, listingId, durationMinutes);

        // Rule 2: seeker's active bookings below the trust-tier max (FR-4.4 rule 2).
        long seekerActive = activeBookingCountForSeeker(seekerId);
        if (seekerActive > seeker.getMaxConcurrentBookings()) {
            throw new BookingRejectedException("Seeker booking limit reached for tier " + seeker.getTrustTier());
        }

        // Rule 3: provider's active bookings below capacity.
        Provider provider = providers.findById(listing.getProviderId())
                .orElseThrow(() -> new BookingRejectedException("Unknown provider for listing"));
        long providerActive = activeBookingCountForProvider(listing.getProviderId());
        if (providerActive >= provider.getCapacity()) {
            throw new BookingRejectedException("Provider is at capacity");
        }

        // Rule 5: total price within the wallet balance.
        double price = pricing.priceFor(booking, listing, seeker);
        if (price > seeker.getBalance()) {
            throw new BookingRejectedException("Insufficient balance for booking");
        }

        // All conditions met: charge, confirm, mark listing booked, persist, notify.
        seeker.charge(price);
        booking.setPrice(price);
        booking.transitionTo(BookingStatus.CONFIRMED);
        listing.transitionTo(ListingStatus.BOOKED);

        seekers.save(seeker);
        listings.save(listing);
        bookings.save(booking);

        notifications.sendBookingConfirmed(seeker, booking);
        return booking;
    }

    private long activeBookingCountForSeeker(String seekerId) {
        return bookings.findBySeekerId(seekerId).stream()
                .filter(Booking::isActive)
                .count();
    }

    private long activeBookingCountForProvider(String providerId) {
        return listings.findByProviderId(providerId).stream()
                .flatMap(l -> bookings.findByListingId(l.getId()).stream())
                .filter(Booking::isActive)
                .count();
    }

    /** Thrown when a booking request fails one of the FR-4.4 conditions. */
    public static class BookingRejectedException extends RuntimeException {
        public BookingRejectedException(String message) {
            super(message);
        }
    }
}
