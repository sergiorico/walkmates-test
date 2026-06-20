package com.walkmates.repository.inmemory;

import com.walkmates.model.Provider;
import com.walkmates.repository.ProviderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory {@link ProviderRepository}. */
@Repository
public class InMemoryProviderRepository implements ProviderRepository {

    private final Map<String, Provider> byId = new ConcurrentHashMap<>();

    @Override
    public Provider save(Provider provider) {
        byId.put(provider.getId(), provider);
        return provider;
    }

    @Override
    public Optional<Provider> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }
}
