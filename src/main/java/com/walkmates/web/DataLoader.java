package com.walkmates.web;

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

/**
 * Seeds a little sample data at startup so the demo UI has Providers, Listings and a funded
 * Seeker to play with. Not used by tests (tests build their own fixtures).
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final SeekerRepository seekers;
    private final ProviderRepository providers;
    private final ListingRepository listings;

    public DataLoader(SeekerRepository seekers, ProviderRepository providers, ListingRepository listings) {
        this.seekers = seekers;
        this.providers = providers;
        this.listings = listings;
    }

    @Override
    public void run(String... args) {
        Provider rex = providers.save(new Provider("Rex's family", 62.39, 17.31, 3));
        Provider shelter = providers.save(new Provider("Sundsvall Animal Shelter", 62.40, 17.32, 10));

        Listing dogWalk = listings.save(new Listing(rex.getId(), "Morning walk with Rex",
                "Friendly golden retriever, loves the riverside path.", ListingType.DOG_WALK));
        listings.save(new Listing(rex.getId(), "Weekend pet-sitting for Rex",
                "Two nights while the family is away.", ListingType.PET_SITTING));
        listings.save(new Listing(shelter.getId(), "Volunteer cat-cuddling afternoon",
                "Help socialise rescue cats. No experience needed.", ListingType.SHELTER_VOLUNTEER));

        Seeker seeker = new Seeker("demo@walkmates.test", "Demo Seeker", "0701234567");
        seeker.setTrustTier(TrustTier.VERIFIED);
        seeker.addFunds(500.00);
        seekers.save(seeker);

        // Print the seeded identifiers so the demo/API flows (booking, AI explain) are
        // immediately drivable — IDs are random UUIDs and there is no discovery endpoint yet.
        log.info("Seeded demo seeker id={} (tier={}, balance={} SEK)",
                seeker.getId(), seeker.getTrustTier(), seeker.getBalance());
        log.info("Try it: curl 'http://localhost:8080/api/match/{}/explain?listingId={}'",
                seeker.getId(), dogWalk.getId());
    }
}
