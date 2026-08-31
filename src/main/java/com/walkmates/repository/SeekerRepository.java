package com.walkmates.repository;

import com.walkmates.model.Seeker;

import java.util.Optional;

/** Data access for {@link Seeker}s. */
public interface SeekerRepository {
    Seeker save(Seeker seeker);

    Optional<Seeker> findById(String id);

    Optional<Seeker> findByEmail(String email);
}
