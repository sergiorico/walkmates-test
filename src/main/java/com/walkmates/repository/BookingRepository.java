package com.walkmates.repository;

import com.walkmates.model.Booking;

import java.util.List;
import java.util.Optional;

/** Data access for {@link Booking}s. */
public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(String id);

    List<Booking> findAll();

    /** All bookings made by a given Seeker. */
    List<Booking> findBySeekerId(String seekerId);

    /** All bookings for a given Listing. */
    List<Booking> findByListingId(String listingId);
}
