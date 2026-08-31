package com.walkmates.catalog;

import java.util.UUID;

/**
 * A pet managed by a Provider through the demo catalog UI.
 *
 * <p>This small catalog model is intentionally separate from the frozen lab domain. It can
 * evolve with the application UI without changing the classes or mutation baseline used by
 * Labs 1–3.</p>
 */
public class Pet {

    public static final int MAX_NAME_LENGTH = 40;
    public static final int MAX_BREED_LENGTH = 60;
    public static final int MAX_AGE_YEARS = 40;
    public static final int MAX_NOTES_LENGTH = 500;

    private final String id;
    private final String providerId;
    private String name;
    private PetSpecies species;
    private String breed;
    private int ageYears;
    private String notes;

    public Pet(String providerId, String name, PetSpecies species, String breed,
               int ageYears, String notes) {
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("Pet must belong to a provider");
        }
        this.id = UUID.randomUUID().toString();
        this.providerId = providerId;
        updateDetails(name, species, breed, ageYears, notes);
    }

    public String getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = validatedName(name);
    }

    private static String validatedName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pet name is required");
        }
        String cleaned = name.strip();
        if (cleaned.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Pet name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        return cleaned;
    }

    public PetSpecies getSpecies() {
        return species;
    }

    public final void setSpecies(PetSpecies species) {
        this.species = validatedSpecies(species);
    }

    private static PetSpecies validatedSpecies(PetSpecies species) {
        if (species == null) {
            throw new IllegalArgumentException("Pet species is required");
        }
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public final void setBreed(String breed) {
        this.breed = validatedBreed(breed);
    }

    private static String validatedBreed(String breed) {
        String cleaned = breed == null ? "" : breed.strip();
        if (cleaned.length() > MAX_BREED_LENGTH) {
            throw new IllegalArgumentException("Breed must be at most " + MAX_BREED_LENGTH + " characters");
        }
        return cleaned;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public final void setAgeYears(int ageYears) {
        this.ageYears = validatedAgeYears(ageYears);
    }

    private static int validatedAgeYears(int ageYears) {
        if (ageYears < 0 || ageYears > MAX_AGE_YEARS) {
            throw new IllegalArgumentException("Age must be between 0 and " + MAX_AGE_YEARS + " years");
        }
        return ageYears;
    }

    public String getNotes() {
        return notes;
    }

    public final void setNotes(String notes) {
        this.notes = validatedNotes(notes);
    }

    private static String validatedNotes(String notes) {
        String cleaned = notes == null ? "" : notes.strip();
        if (cleaned.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("Notes must be at most " + MAX_NOTES_LENGTH + " characters");
        }
        return cleaned;
    }

    /** Validates all fields before applying them, so a failed edit never leaves partial changes. */
    public final void updateDetails(String name, PetSpecies species, String breed,
                                    int ageYears, String notes) {
        String cleanName = validatedName(name);
        PetSpecies cleanSpecies = validatedSpecies(species);
        String cleanBreed = validatedBreed(breed);
        int cleanAge = validatedAgeYears(ageYears);
        String cleanNotes = validatedNotes(notes);

        this.name = cleanName;
        this.species = cleanSpecies;
        this.breed = cleanBreed;
        this.ageYears = cleanAge;
        this.notes = cleanNotes;
    }
}
