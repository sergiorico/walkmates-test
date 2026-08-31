package com.walkmates.repository;

import com.walkmates.model.Listing;

import java.util.List;
import java.util.Optional;

/** Data access for {@link Listing}s. */
public interface ListingRepository {
    Listing save(Listing listing);

    Optional<Listing> findById(String id);

    List<Listing> findAll();

    List<Listing> findByProviderId(String providerId);

    void deleteById(String id);
}
