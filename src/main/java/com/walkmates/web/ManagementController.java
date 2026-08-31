package com.walkmates.web;

import com.walkmates.catalog.Pet;
import com.walkmates.catalog.PetRepository;
import com.walkmates.catalog.PetSpecies;
import com.walkmates.catalog.persistence.CatalogPersistence;
import com.walkmates.demoauth.DemoAccountService;
import com.walkmates.demoauth.DemoPrincipal;
import com.walkmates.demoauth.DemoRole;
import com.walkmates.model.Booking;
import com.walkmates.model.BookingStatus;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingStatus;
import com.walkmates.model.ListingType;
import com.walkmates.model.Provider;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Provider-facing CRUD interface for the evolving demo catalog. */
@Controller
@RequestMapping("/manage")
public class ManagementController {

    private static final Logger log = LoggerFactory.getLogger(ManagementController.class);
    private static final int MAX_LISTING_TITLE_LENGTH = 255;
    private static final int MAX_LISTING_DESCRIPTION_LENGTH = 2_000;

    private final ProviderRepository providers;
    private final ListingRepository listings;
    private final BookingRepository bookings;
    private final SeekerRepository seekers;
    private final PetRepository pets;
    private final CatalogPersistence catalog;
    private final DemoAccountService demoAccounts;

    public ManagementController(ProviderRepository providers,
                                ListingRepository listings,
                                BookingRepository bookings,
                                SeekerRepository seekers,
                                PetRepository pets,
                                CatalogPersistence catalog,
                                DemoAccountService demoAccounts) {
        this.providers = providers;
        this.listings = listings;
        this.bookings = bookings;
        this.seekers = seekers;
        this.pets = pets;
        this.catalog = catalog;
        this.demoAccounts = demoAccounts;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) String providerId,
                            HttpSession session,
                            Model model) {
        DemoPrincipal account = demoAccounts.current(session).orElse(null);
        if (!demoAccounts.canManageAnyProvider(account)) {
            return "redirect:/?login=required#demo-access";
        }

        List<Provider> allProviders = providers.findAll().stream()
                .sorted(Comparator.comparing(Provider::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<Provider> accessibleProviders = account.role() == DemoRole.ADMIN
                ? allProviders
                : allProviders.stream()
                .filter(provider -> demoAccounts.canManageProvider(account, provider))
                .toList();
        Provider selected = selectProvider(providerId, accessibleProviders);

        model.addAttribute("providers", accessibleProviders);
        model.addAttribute("selectedProvider", selected);
        model.addAttribute("account", account);
        model.addAttribute("canSwitchProviders", account.role() == DemoRole.ADMIN);
        model.addAttribute("speciesTypes", PetSpecies.values());
        model.addAttribute("listingTypes", ListingType.values());

        List<Pet> selectedPets;
        List<Listing> selectedListings;
        if (selected != null) {
            selectedPets = pets.findByProviderId(selected.getId()).stream()
                    .sorted(Comparator.comparing(Pet::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            selectedListings = listings.findByProviderId(selected.getId()).stream()
                    .sorted(Comparator.comparing(Listing::getTitle, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } else {
            selectedPets = List.of();
            selectedListings = List.of();
        }

        Map<String, Pet> petsById = new HashMap<>();
        Map<String, Integer> petServiceCounts = new HashMap<>();
        for (Pet pet : selectedPets) {
            petsById.put(pet.getId(), pet);
            petServiceCounts.put(pet.getId(), catalog.serviceCountForPet(pet.getId()));
        }
        Map<String, String> listingPetIds = new HashMap<>();
        Map<String, Integer> listingBookingCounts = new HashMap<>();
        for (Listing listing : selectedListings) {
            catalog.linkedPetId(listing.getId())
                    .filter(petsById::containsKey)
                    .ifPresent(petId -> listingPetIds.put(listing.getId(), petId));
            listingBookingCounts.put(listing.getId(), bookings.findByListingId(listing.getId()).size());
        }
        List<BookingCardView> providerBookings = selected == null
                ? List.of()
                : selectedListings.stream()
                .flatMap(listing -> bookings.findByListingId(listing.getId()).stream()
                        .map(booking -> toBookingCard(booking, listing, selected)))
                .sorted(Comparator.comparing(BookingCardView::getListingTitle,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
        model.addAttribute("pets", selectedPets);
        model.addAttribute("listings", selectedListings);
        model.addAttribute("bookings", providerBookings);
        model.addAttribute("activeBookingCount", providerBookings.stream()
                .filter(booking -> booking.getStatus().isActive())
                .count());
        model.addAttribute("petsById", petsById);
        model.addAttribute("petServiceCounts", petServiceCounts);
        model.addAttribute("listingPetIds", listingPetIds);
        model.addAttribute("listingBookingCounts", listingBookingCounts);
        return "manage";
    }

    @PostMapping("/pets")
    public String createPet(@RequestParam String providerId,
                            @RequestParam String name,
                            @RequestParam PetSpecies species,
                            @RequestParam(defaultValue = "") String breed,
                            @RequestParam int ageYears,
                            @RequestParam(defaultValue = "") String notes,
                            HttpSession session,
                            RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Pet added", () -> {
            requireProvider(providerId);
            Pet pet = new Pet(providerId, name, species, breed, ageYears, notes);
            catalog.savePet(pet);
            pets.save(pet);
        });
    }

    @PostMapping("/pets/{petId}/edit")
    public String editPet(@PathVariable String petId,
                          @RequestParam String providerId,
                          @RequestParam String name,
                          @RequestParam PetSpecies species,
                          @RequestParam(defaultValue = "") String breed,
                          @RequestParam int ageYears,
                          @RequestParam(defaultValue = "") String notes,
                          HttpSession session,
                          RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Pet updated", () -> {
            Pet pet = requireOwnedPet(petId, providerId);
            pet.updateDetails(name, species, breed, ageYears, notes);
            catalog.savePet(pet);
            pets.save(pet);
        });
    }

    @PostMapping("/pets/{petId}/delete")
    public String deletePet(@PathVariable String petId,
                            @RequestParam String providerId,
                            HttpSession session,
                            RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Pet removed", () -> {
            Pet pet = requireOwnedPet(petId, providerId);
            catalog.deletePet(pet);
            pets.deleteById(petId);
        });
    }

    @PostMapping("/listings")
    public String createListing(@RequestParam String providerId,
                                @RequestParam String title,
                                @RequestParam(defaultValue = "") String description,
                                @RequestParam ListingType type,
                                @RequestParam(defaultValue = "") String petId,
                                HttpSession session,
                                RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Service listing added", () -> {
            requireProvider(providerId);
            String linkedPetId = optionalOwnedPetId(petId, providerId);
            validateListingFields(title, description);
            Listing listing = new Listing(providerId, title, description, type);
            catalog.saveListing(listing, linkedPetId);
            listings.save(listing);
        });
    }

    @PostMapping("/listings/{listingId}/edit")
    public String editListing(@PathVariable String listingId,
                              @RequestParam String providerId,
                              @RequestParam String title,
                              @RequestParam(defaultValue = "") String description,
                              @RequestParam ListingType type,
                              @RequestParam(defaultValue = "") String petId,
                              HttpSession session,
                              RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Service listing updated", () -> {
            Listing listing = requireOwnedListing(listingId, providerId);
            String linkedPetId = optionalOwnedPetId(petId, providerId);
            validateListingFields(title, description);
            listing.setTitle(title);
            listing.setDescription(description);
            listing.setType(type);
            catalog.saveListing(listing, linkedPetId);
            listings.save(listing);
        });
    }

    @PostMapping("/listings/{listingId}/delete")
    public String deleteListing(@PathVariable String listingId,
                                @RequestParam String providerId,
                                HttpSession session,
                                RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Service listing removed", () -> {
            Listing listing = requireOwnedListing(listingId, providerId);
            if (!listing.isAvailable()) {
                throw new IllegalArgumentException("Booked listings cannot be removed");
            }
            if (!bookings.findByListingId(listingId).isEmpty()) {
                throw new IllegalArgumentException("Services with booking history cannot be removed");
            }
            catalog.deleteListing(listing);
            listings.deleteById(listingId);
        });
    }

    @PostMapping("/bookings/{bookingId}/advance")
    public String advanceBooking(@PathVariable String bookingId,
                                 @RequestParam String providerId,
                                 @RequestParam BookingStatus expectedStatus,
                                 HttpSession session,
                                 RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Booking moved to its next stage", () -> {
            Booking booking = bookings.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            Listing listing = requireOwnedListing(booking.getListingId(), providerId);
            if (booking.getStatus() != expectedStatus) {
                throw new IllegalArgumentException("Booking status has already changed");
            }

            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                if (listing.getStatus() != ListingStatus.BOOKED) {
                    throw new IllegalStateException("Booking and service statuses do not match");
                }
                booking.transitionTo(BookingStatus.IN_PROGRESS);
                listing.transitionTo(ListingStatus.IN_PROGRESS);
            } else if (booking.getStatus() == BookingStatus.IN_PROGRESS) {
                if (listing.getStatus() != ListingStatus.IN_PROGRESS) {
                    throw new IllegalStateException("Booking and service statuses do not match");
                }
                booking.transitionTo(BookingStatus.COMPLETED);
                listing.transitionTo(ListingStatus.COMPLETED);
            } else {
                throw new IllegalArgumentException("This booking cannot move to another stage");
            }

            bookings.save(booking);
            listings.save(listing);
        });
    }

    @PostMapping("/listings/{listingId}/reopen")
    public String reopenListing(@PathVariable String listingId,
                                @RequestParam String providerId,
                                HttpSession session,
                                RedirectAttributes redirect) {
        return perform(providerId, session, redirect, "Service reopened for a new booking", () -> {
            Listing listing = requireOwnedListing(listingId, providerId);
            listing.transitionTo(ListingStatus.AVAILABLE);
            listings.save(listing);
        });
    }

    private Provider selectProvider(String providerId, List<Provider> accessibleProviders) {
        if (providerId == null || providerId.isBlank()) {
            return accessibleProviders.stream().findFirst().orElse(null);
        }
        return accessibleProviders.stream()
                .filter(provider -> provider.getId().equals(providerId))
                .findFirst()
                .orElseGet(() -> accessibleProviders.stream().findFirst().orElse(null));
    }

    private Provider requireProvider(String providerId) {
        return providers.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
    }

    private Pet requireOwnedPet(String petId, String providerId) {
        requireProvider(providerId);
        Pet pet = pets.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        if (!pet.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("Pet does not belong to this provider");
        }
        return pet;
    }

    private Listing requireOwnedListing(String listingId, String providerId) {
        requireProvider(providerId);
        Listing listing = listings.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Service listing not found"));
        if (!listing.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("Service listing does not belong to this provider");
        }
        return listing;
    }

    private String optionalOwnedPetId(String petId, String providerId) {
        if (petId == null || petId.isBlank()) {
            return null;
        }
        return requireOwnedPet(petId, providerId).getId();
    }

    private BookingCardView toBookingCard(Booking booking, Listing listing, Provider provider) {
        String petName = catalog.linkedPetId(listing.getId())
                .flatMap(pets::findById)
                .map(Pet::getName)
                .orElse("");
        String seekerName = seekers.findById(booking.getSeekerId())
                .map(seeker -> seeker.getDisplayName())
                .orElse("Seeker");
        return new BookingCardView(booking, listing, petName, seekerName, provider.getName());
    }

    private String perform(String providerId, HttpSession session, RedirectAttributes redirect,
                           String successMessage, Runnable action) {
        try {
            Provider provider = requireProvider(providerId);
            DemoPrincipal account = demoAccounts.current(session)
                    .orElseThrow(() -> new IllegalArgumentException("Sign in to manage a provider"));
            if (!demoAccounts.canManageProvider(account, provider)) {
                throw new IllegalArgumentException("This account cannot manage that provider");
            }
            action.run();
            redirect.addFlashAttribute("successMessage", successMessage);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirect.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            log.error("Catalog operation failed", exception);
            redirect.addFlashAttribute("errorMessage", "Catalog storage is temporarily unavailable");
        }
        return "redirect:/manage?providerId=" + providerId;
    }

    private void validateListingFields(String title, String description) {
        if (title != null && title.strip().length() > MAX_LISTING_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Service title must be at most " + MAX_LISTING_TITLE_LENGTH + " characters");
        }
        if (description != null && description.length() > MAX_LISTING_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description must be at most " + MAX_LISTING_DESCRIPTION_LENGTH + " characters");
        }
    }
}
