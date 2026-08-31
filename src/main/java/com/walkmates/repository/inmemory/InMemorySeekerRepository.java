package com.walkmates.repository.inmemory;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory {@link SeekerRepository}. Default persistence for the labs. */
@Repository
public class InMemorySeekerRepository implements SeekerRepository {

    private final Map<String, Seeker> byId = new ConcurrentHashMap<>();

    @Override
    public Seeker save(Seeker seeker) {
        byId.put(seeker.getId(), seeker);
        return seeker;
    }

    @Override
    public Optional<Seeker> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Seeker> findByEmail(String email) {
        return byId.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
