package com.walkmates.catalog;

import java.util.List;
import java.util.Optional;

/** Data access for pets shown in the provider-management demo. */
public interface PetRepository {
    Pet save(Pet pet);

    Optional<Pet> findById(String id);

    List<Pet> findByProviderId(String providerId);

    void deleteById(String id);
}
