package com.walkmates.repository.inmemory;

import com.walkmates.model.Listing;
import com.walkmates.repository.ListingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory {@link ListingRepository}. */
@Repository
public class InMemoryListingRepository implements ListingRepository {

    private final Map<String, Listing> byId = new ConcurrentHashMap<>();

    @Override
    public Listing save(Listing listing) {
        byId.put(listing.getId(), listing);
        return listing;
    }

    @Override
    public Optional<Listing> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Listing> findAll() {
        return List.copyOf(byId.values());
    }

    @Override
    public List<Listing> findByProviderId(String providerId) {
        return byId.values().stream()
                .filter(l -> l.getProviderId().equals(providerId))
                .toList();
    }
}
