package com.walkmates.repository;

import com.walkmates.model.Provider;

import java.util.List;
import java.util.Optional;

/** Data access for {@link Provider}s. */
public interface ProviderRepository {
    Provider save(Provider provider);

    Optional<Provider> findById(String id);

    List<Provider> findAll();
}
