package com.walkmates.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * A Seeker is a person who wants time with animals and books {@link Listing}s.
 *
 * <p>This is the <strong>Lab 1 specification-based testing target</strong>: its public API
 * ({@link #addFunds(double)}, {@link #getTrustTier()}, validation in the constructor and
 * setters) is small, has clear numeric boundaries from REQUIREMENTS FR-1.1 / FR-1.3, and needs
 * no other objects to test. It is intentionally kept <em>correct</em> so well-designed
 * equivalence-partition and boundary-value tests pass.</p>
 */
public class Seeker {

    /** Wallet limits (FR-1.3), in SEK. */
    public static final double MIN_TOP_UP = 10.00;
    public static final double MAX_SINGLE_TOP_UP = 5_000.00;
    public static final double MAX_BALANCE = 20_000.00;

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 40;

    // One '@', a non-empty local part, and a domain that contains at least one dot (FR-1.1).
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    // Letters (incl. Swedish), spaces, hyphens, apostrophes only (FR-1.1).
    private static final Pattern NAME = Pattern.compile("^[\\p{L} '\\-]+$");
    // Swedish 07XXXXXXXX (10 digits) or international +467XXXXXXXX (FR-1.1).
    private static final Pattern PHONE = Pattern.compile("^(07\\d{8}|\\+467\\d{7})$");

    private final String id;
    private String email;
    private String displayName;
    private String phoneNumber;
    private TrustTier trustTier;
    private double balance;

    /**
     * Registers a new Seeker. Starts at {@link TrustTier#NEW} with a zero balance (FR-1.2).
     *
     * @param email       unique email, valid format, max 254 chars
     * @param displayName 2-40 chars, letters/spaces/hyphens/apostrophes
     * @param phoneNumber Swedish or international format
     * @throws IllegalArgumentException if any field violates FR-1.1
     */
    public Seeker(String email, String displayName, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        setEmail(email);
        setDisplayName(displayName);
        setPhoneNumber(phoneNumber);
        this.trustTier = TrustTier.NEW;
        this.balance = 0.00;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Email must be at most " + MAX_EMAIL_LENGTH + " characters");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String displayName) {
        if (displayName == null) {
            throw new IllegalArgumentException("Display name is required");
        }
        if (displayName.length() < MIN_NAME_LENGTH || displayName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Display name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
        }
        if (!NAME.matcher(displayName).matches()) {
            throw new IllegalArgumentException("Display name contains invalid characters");
        }
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Phone number format is invalid");
        }
        this.phoneNumber = phoneNumber;
    }

    public TrustTier getTrustTier() {
        return trustTier;
    }

    public void setTrustTier(TrustTier trustTier) {
        if (trustTier == null) {
            throw new IllegalArgumentException("Trust tier is required");
        }
        this.trustTier = trustTier;
    }

    public double getBalance() {
        return balance;
    }

    /** Maximum concurrent active bookings allowed for this Seeker's tier (FR-1.2). */
    public int getMaxConcurrentBookings() {
        return trustTier.getMaxConcurrentBookings();
    }

    /**
     * Adds funds to the wallet (FR-1.3). Validates the per-transaction minimum and maximum and
     * the resulting maximum balance. On any violation the balance is left unchanged.
     *
     * @param amount amount in SEK to add
     * @throws IllegalArgumentException if {@code amount} is below {@link #MIN_TOP_UP}, above
     *                                  {@link #MAX_SINGLE_TOP_UP}, or would push the balance
     *                                  above {@link #MAX_BALANCE}
     */
    public void addFunds(double amount) {
        if (amount < MIN_TOP_UP) {
            throw new IllegalArgumentException("Minimum top-up is " + MIN_TOP_UP + " SEK");
        }
        if (amount > MAX_SINGLE_TOP_UP) {
            throw new IllegalArgumentException("Maximum single top-up is " + MAX_SINGLE_TOP_UP + " SEK");
        }
        double newBalance = round2(balance + amount);
        if (newBalance > MAX_BALANCE) {
            throw new IllegalArgumentException("Balance may not exceed " + MAX_BALANCE + " SEK");
        }
        this.balance = newBalance;
    }

    /**
     * Deducts an amount from the wallet for a booking (FR-1.3 / FR-4.3). The balance may never
     * go negative.
     *
     * @param amount amount in SEK to charge
     * @throws IllegalArgumentException if {@code amount} is negative or exceeds the balance
     */
    public void charge(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Charge amount cannot be negative");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = round2(balance - amount);
    }

    /** Rounds a SEK amount to 2 decimal places, half-up (FR-1.3). */
    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
