package com.walkmates.catalog.inmemory;

import com.walkmates.catalog.Pet;
import com.walkmates.catalog.PetRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Runtime pet repository; provider-managed definitions are restored from the H2 catalog. */
@Repository
public class InMemoryPetRepository implements PetRepository {

    private final Map<String, Pet> byId = new ConcurrentHashMap<>();

    @Override
    public Pet save(Pet pet) {
        byId.put(pet.getId(), pet);
        return pet;
    }

    @Override
    public Optional<Pet> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<Pet> findByProviderId(String providerId) {
        return byId.values().stream()
                .filter(pet -> pet.getProviderId().equals(providerId))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        byId.remove(id);
    }
}
