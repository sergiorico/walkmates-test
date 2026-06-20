package com.walkmates.repository.inmemory;

import com.walkmates.model.Booking;
import com.walkmates.repository.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory {@link BookingRepository}. */
@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> byId = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        byId.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return List.copyOf(byId.values());
    }

    @Override
    public List<Booking> findBySeekerId(String seekerId) {
        return byId.values().stream()
                .filter(b -> b.getSeekerId().equals(seekerId))
                .toList();
    }

    @Override
    public List<Booking> findByListingId(String listingId) {
        return byId.values().stream()
                .filter(b -> b.getListingId().equals(listingId))
                .toList();
    }
}
