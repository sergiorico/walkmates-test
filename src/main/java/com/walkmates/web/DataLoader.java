package com.walkmates.web;

import com.walkmates.catalog.Pet;
import com.walkmates.catalog.PetRepository;
import com.walkmates.catalog.PetSpecies;
import com.walkmates.catalog.persistence.CatalogPersistence;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Provider;
import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds a little sample data at startup so the demo UI has Providers, Listings and a funded
 * Seeker to play with. Not used by tests (tests build their own fixtures).
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String REX_FAMILY_KEY = "rex-family";
    private static final String OESTERSUND_SHELTER_KEY = "ostersund-animal-shelter";
    private static final String REX_LINK_MIGRATION = "2026-08-31-link-rex-services";
    private static final String SHELTER_LINK_MIGRATION = "2026-08-31-link-shelter-services";

    private final SeekerRepository seekers;
    private final ProviderRepository providers;
    private final ListingRepository listings;
    private final PetRepository pets;
    private final CatalogPersistence catalog;

    public DataLoader(SeekerRepository seekers, ProviderRepository providers,
                      ListingRepository listings, PetRepository pets,
                      CatalogPersistence catalog) {
        this.seekers = seekers;
        this.providers = providers;
        this.listings = listings;
        this.pets = pets;
        this.catalog = catalog;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Provider rex = providers.save(new Provider("Rex's family", 63.18, 14.64, 3));
        Provider shelter = providers.save(new Provider("Östersund Animal Shelter", 63.17, 14.65, 10));

        catalog.registerProvider(REX_FAMILY_KEY, rex);
        catalog.registerProvider(OESTERSUND_SHELTER_KEY, shelter);

        initializeProviderCatalog(REX_FAMILY_KEY, rex,
                List.of(new Pet(rex.getId(), "Rex", PetSpecies.DOG, "Golden retriever", 5,
                        "Friendly, energetic, and happiest near the lakeside path.")),
                List.of(
                        new Listing(rex.getId(), "Morning walk with Rex",
                                "Friendly golden retriever, loves the Storsjön lakeside path.",
                                ListingType.DOG_WALK),
                        new Listing(rex.getId(), "Weekend pet-sitting for Rex",
                                "Two nights while the family is away.", ListingType.PET_SITTING)));
        linkSeededServicesOnce(REX_LINK_MIGRATION, rex.getId(), "Rex",
                List.of("Morning walk with Rex", "Weekend pet-sitting for Rex"));

        initializeProviderCatalog(OESTERSUND_SHELTER_KEY, shelter,
                List.of(new Pet(shelter.getId(), "Morrhår", PetSpecies.CAT, "Domestic shorthair", 3,
                        "A calm rescue cat who enjoys patient company.")),
                List.of(new Listing(shelter.getId(), "Volunteer cat-cuddling afternoon",
                        "Help socialise rescue cats. No experience needed.",
                        ListingType.SHELTER_VOLUNTEER)));
        linkSeededServicesOnce(SHELTER_LINK_MIGRATION, shelter.getId(), "Morrhår",
                List.of("Volunteer cat-cuddling afternoon"));

        Listing exampleListing = listings.findByProviderId(rex.getId()).stream()
                .findFirst()
                .orElse(null);

        Seeker seeker = new Seeker("demo@walkmates.test", "Demo Seeker", "0701234567");
        seeker.setTrustTier(TrustTier.VERIFIED);
        seeker.addFunds(500.00);
        seekers.save(seeker);

        // Print the seeded identifiers so the demo/API flows (booking, AI explain) are
        // immediately drivable — IDs are random UUIDs and there is no discovery endpoint yet.
        log.info("Seeded demo seeker id={} (tier={}, balance={} SEK)",
                seeker.getId(), seeker.getTrustTier(), seeker.getBalance());
        if (exampleListing != null) {
            log.info("Try it: curl 'http://localhost:8080/api/match/{}/explain?listingId={}'",
                    seeker.getId(), exampleListing.getId());
        }
    }

    private void initializeProviderCatalog(String providerKey, Provider provider,
                                           List<Pet> defaultPets, List<Listing> defaultListings) {
        if (catalog.isInitialized(providerKey)) {
            catalog.loadPets(providerKey, provider.getId()).forEach(pets::save);
            catalog.loadListings(providerKey, provider.getId()).forEach(listings::save);
            return;
        }

        defaultPets.forEach(pet -> {
            catalog.savePet(pet);
            pets.save(pet);
        });
        defaultListings.forEach(listing -> {
            catalog.saveListing(listing);
            listings.save(listing);
        });
        catalog.markInitialized(providerKey);
    }

    /** Adds the initial pet relationship once, without restoring it after a provider unlinks it. */
    private void linkSeededServicesOnce(String migrationId, String providerId, String petName,
                                        List<String> listingTitles) {
        if (catalog.isMigrationApplied(migrationId)) {
            return;
        }

        Pet pet = pets.findByProviderId(providerId).stream()
                .filter(candidate -> candidate.getName().equals(petName))
                .findFirst()
                .orElse(null);
        if (pet != null) {
            listings.findByProviderId(providerId).stream()
                    .filter(listing -> listingTitles.contains(listing.getTitle()))
                    .forEach(listing -> catalog.saveListing(listing, pet.getId()));
        }
        catalog.markMigrationApplied(migrationId);
    }
}
