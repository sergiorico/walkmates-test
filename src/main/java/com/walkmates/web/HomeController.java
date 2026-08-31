package com.walkmates.web;

import com.walkmates.catalog.PetRepository;
import com.walkmates.catalog.persistence.CatalogPersistence;
import com.walkmates.demoauth.DemoAccountService;
import com.walkmates.demoauth.DemoPrincipal;
import com.walkmates.demoauth.DemoRole;
import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.Seeker;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Serves the demo home page and connects it to the seeded Seeker. */
@Controller
public class HomeController {

    static final String DEMO_SEEKER_EMAIL = "demo@walkmates.test";

    private final ListingRepository listings;
    private final SeekerRepository seekers;
    private final ProviderRepository providers;
    private final BookingRepository bookings;
    private final PetRepository pets;
    private final CatalogPersistence catalog;
    private final DemoAccountService demoAccounts;

    public HomeController(ListingRepository listings, SeekerRepository seekers,
                          ProviderRepository providers, BookingRepository bookings,
                          PetRepository pets, CatalogPersistence catalog,
                          DemoAccountService demoAccounts) {
        this.listings = listings;
        this.seekers = seekers;
        this.providers = providers;
        this.bookings = bookings;
        this.pets = pets;
        this.catalog = catalog;
        this.demoAccounts = demoAccounts;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "") String booking,
                       @RequestParam(defaultValue = "") String login,
                       @RequestParam(defaultValue = "") String logout,
                       HttpSession session,
                       Model model) {
        List<Listing> allListings = listings.findAll().stream()
                .sorted(Comparator.comparing(Listing::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
        Map<String, String> listingPetNames = new HashMap<>();
        for (Listing listing : allListings) {
            catalog.linkedPetId(listing.getId())
                    .flatMap(pets::findById)
                    .map(pet -> pet.getName())
                    .ifPresent(name -> listingPetNames.put(listing.getId(), name));
        }

        DemoPrincipal account = demoAccounts.current(session).orElse(null);
        Seeker demoSeeker = account != null && account.role() == DemoRole.SEEKER
                ? seekers.findByEmail(DEMO_SEEKER_EMAIL).orElse(null)
                : null;
        List<BookingCardView> bookingCards = demoSeeker == null
                ? List.of()
                : bookings.findBySeekerId(demoSeeker.getId()).stream()
                .map(this::toBookingCard)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(BookingCardView::getListingTitle,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();

        model.addAttribute("listings", allListings);
        model.addAttribute("listingPetNames", listingPetNames);
        model.addAttribute("demoSeeker", demoSeeker);
        model.addAttribute("bookings", bookingCards);
        model.addAttribute("bookingCreated", booking.equals("created"));
        model.addAttribute("account", account);
        model.addAttribute("demoAccounts", demoAccounts.accounts());
        model.addAttribute("loginError", login.equals("error"));
        model.addAttribute("accessRequired", login.equals("required"));
        model.addAttribute("loggedOut", logout.equals("1"));
        return "index";
    }

    private Optional<BookingCardView> toBookingCard(Booking booking) {
        return listings.findById(booking.getListingId()).map(listing -> {
            String petName = catalog.linkedPetId(listing.getId())
                    .flatMap(pets::findById)
                    .map(pet -> pet.getName())
                    .orElse("");
            String providerName = providers.findById(listing.getProviderId())
                    .map(provider -> provider.getName())
                    .orElse("Provider");
            String seekerName = seekers.findById(booking.getSeekerId())
                    .map(Seeker::getDisplayName)
                    .orElse("Seeker");
            return new BookingCardView(booking, listing, petName, seekerName, providerName);
        });
    }
}
