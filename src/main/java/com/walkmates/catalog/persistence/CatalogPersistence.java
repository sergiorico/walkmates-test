package com.walkmates.catalog.persistence;

import com.walkmates.catalog.Pet;
import com.walkmates.catalog.PetSpecies;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Provider;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persists provider-managed catalog definitions without changing the frozen lab repositories.
 *
 * <p>Providers receive stable catalog keys while their domain IDs remain runtime UUIDs. Pets and
 * listings are reconstructed from H2 at startup, then mapped back to their persistent record IDs
 * for later updates and deletes. Booking status is deliberately not stored here.</p>
 */
@Repository
public class CatalogPersistence {

    private final JdbcTemplate jdbc;
    private final Map<String, String> providerKeysByRuntimeId = new ConcurrentHashMap<>();
    private final Map<String, String> petRecordIdsByRuntimeId = new ConcurrentHashMap<>();
    private final Map<String, String> petRuntimeIdsByRecordId = new ConcurrentHashMap<>();
    private final Map<String, String> listingRecordIdsByRuntimeId = new ConcurrentHashMap<>();
    private final Map<String, String> listingPetRuntimeIds = new ConcurrentHashMap<>();

    public CatalogPersistence(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void createSchema() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS catalog_provider_state (
                    provider_key VARCHAR(80) PRIMARY KEY,
                    initialized BOOLEAN NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS catalog_pet (
                    id VARCHAR(36) PRIMARY KEY,
                    provider_key VARCHAR(80) NOT NULL,
                    name VARCHAR(40) NOT NULL,
                    species VARCHAR(20) NOT NULL,
                    breed VARCHAR(60) NOT NULL,
                    age_years INTEGER NOT NULL,
                    notes VARCHAR(500) NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS catalog_listing (
                    id VARCHAR(36) PRIMARY KEY,
                    provider_key VARCHAR(80) NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    description VARCHAR(2000) NOT NULL,
                    listing_type VARCHAR(40) NOT NULL,
                    pet_id VARCHAR(36)
                )
                """);
        jdbc.execute("ALTER TABLE catalog_listing ADD COLUMN IF NOT EXISTS pet_id VARCHAR(36)");
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS catalog_migration (
                    migration_id VARCHAR(120) PRIMARY KEY,
                    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        // One-time-compatible seed correction for catalogs created before publication. The
        // exact seed fields avoid renaming a provider-created pet that merely shares the name.
        jdbc.update("""
                UPDATE catalog_pet SET name = 'Morrhår'
                WHERE provider_key = 'ostersund-animal-shelter'
                  AND name = 'Maja'
                  AND species = 'CAT'
                  AND breed = 'Domestic shorthair'
                  AND notes = 'A calm rescue cat who enjoys patient company.'
                """);
    }

    public void registerProvider(String providerKey, Provider provider) {
        if (providerKey == null || providerKey.isBlank() || provider == null) {
            throw new IllegalArgumentException("Provider key and provider are required");
        }
        providerKeysByRuntimeId.put(provider.getId(), providerKey);
    }

    public boolean isInitialized(String providerKey) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM catalog_provider_state WHERE provider_key = ? AND initialized = TRUE",
                Integer.class, providerKey);
        return count != null && count > 0;
    }

    public void markInitialized(String providerKey) {
        jdbc.update("MERGE INTO catalog_provider_state (provider_key, initialized) KEY(provider_key) VALUES (?, TRUE)",
                providerKey);
    }

    public boolean isMigrationApplied(String migrationId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM catalog_migration WHERE migration_id = ?",
                Integer.class, migrationId);
        return count != null && count > 0;
    }

    public void markMigrationApplied(String migrationId) {
        jdbc.update("MERGE INTO catalog_migration (migration_id) KEY(migration_id) VALUES (?)",
                migrationId);
    }

    public List<Pet> loadPets(String providerKey, String runtimeProviderId) {
        return jdbc.query("""
                        SELECT id, name, species, breed, age_years, notes
                        FROM catalog_pet WHERE provider_key = ? ORDER BY LOWER(name), id
                        """,
                (result, rowNumber) -> {
                    Pet pet = new Pet(runtimeProviderId,
                            result.getString("name"),
                            PetSpecies.valueOf(result.getString("species")),
                            result.getString("breed"),
                            result.getInt("age_years"),
                            result.getString("notes"));
                    String recordId = result.getString("id");
                    petRecordIdsByRuntimeId.put(pet.getId(), recordId);
                    petRuntimeIdsByRecordId.put(recordId, pet.getId());
                    return pet;
                }, providerKey);
    }

    public List<Listing> loadListings(String providerKey, String runtimeProviderId) {
        return jdbc.query("""
                        SELECT id, title, description, listing_type, pet_id
                        FROM catalog_listing WHERE provider_key = ? ORDER BY LOWER(title), id
                        """,
                (result, rowNumber) -> {
                    Listing listing = new Listing(runtimeProviderId,
                            result.getString("title"),
                            result.getString("description"),
                            ListingType.valueOf(result.getString("listing_type")));
                    listingRecordIdsByRuntimeId.put(listing.getId(), result.getString("id"));
                    String petRecordId = result.getString("pet_id");
                    String petRuntimeId = petRecordId == null
                            ? null
                            : petRuntimeIdsByRecordId.get(petRecordId);
                    if (petRuntimeId != null) {
                        listingPetRuntimeIds.put(listing.getId(), petRuntimeId);
                    }
                    return listing;
                }, providerKey);
    }

    public void savePet(Pet pet) {
        String providerKey = requireProviderKey(pet.getProviderId());
        String recordId = petRecordIdsByRuntimeId.computeIfAbsent(pet.getId(), ignored -> pet.getId());
        petRuntimeIdsByRecordId.put(recordId, pet.getId());
        jdbc.update("""
                        MERGE INTO catalog_pet
                        (id, provider_key, name, species, breed, age_years, notes) KEY(id)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,
                recordId, providerKey, pet.getName(), pet.getSpecies().name(), pet.getBreed(),
                pet.getAgeYears(), pet.getNotes());
    }

    public void deletePet(Pet pet) {
        if (serviceCountForPet(pet.getId()) > 0) {
            throw new IllegalArgumentException("Reassign or remove this pet's services first");
        }
        String recordId = petRecordIdsByRuntimeId.getOrDefault(pet.getId(), pet.getId());
        jdbc.update("DELETE FROM catalog_pet WHERE id = ?", recordId);
        petRecordIdsByRuntimeId.remove(pet.getId());
        petRuntimeIdsByRecordId.remove(recordId);
    }

    public void saveListing(Listing listing) {
        saveListing(listing, linkedPetId(listing.getId()).orElse(null));
    }

    public void saveListing(Listing listing, String runtimePetId) {
        String providerKey = requireProviderKey(listing.getProviderId());
        String recordId = listingRecordIdsByRuntimeId.computeIfAbsent(
                listing.getId(), ignored -> listing.getId());
        String petRecordId = null;
        if (runtimePetId != null && !runtimePetId.isBlank()) {
            petRecordId = petRecordIdsByRuntimeId.get(runtimePetId);
            if (petRecordId == null) {
                throw new IllegalArgumentException("Selected pet is not stored in the catalog");
            }
            listingPetRuntimeIds.put(listing.getId(), runtimePetId);
        } else {
            listingPetRuntimeIds.remove(listing.getId());
        }
        jdbc.update("""
                        MERGE INTO catalog_listing
                        (id, provider_key, title, description, listing_type, pet_id) KEY(id)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                recordId, providerKey, listing.getTitle(), listing.getDescription(),
                listing.getType().name(), petRecordId);
    }

    public Optional<String> linkedPetId(String runtimeListingId) {
        return Optional.ofNullable(listingPetRuntimeIds.get(runtimeListingId));
    }

    public int serviceCountForPet(String runtimePetId) {
        String petRecordId = petRecordIdsByRuntimeId.get(runtimePetId);
        if (petRecordId == null) {
            return 0;
        }
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM catalog_listing WHERE pet_id = ?",
                Integer.class, petRecordId);
        return count == null ? 0 : count;
    }

    public void deleteListing(Listing listing) {
        String recordId = listingRecordIdsByRuntimeId.getOrDefault(listing.getId(), listing.getId());
        jdbc.update("DELETE FROM catalog_listing WHERE id = ?", recordId);
        listingRecordIdsByRuntimeId.remove(listing.getId());
        listingPetRuntimeIds.remove(listing.getId());
    }

    private String requireProviderKey(String runtimeProviderId) {
        String providerKey = providerKeysByRuntimeId.get(runtimeProviderId);
        if (providerKey == null) {
            throw new IllegalStateException("Provider is not registered with the catalog");
        }
        return providerKey;
    }
}
