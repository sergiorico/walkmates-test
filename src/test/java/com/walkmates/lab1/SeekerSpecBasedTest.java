package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Lab 1, Part B — specification-based tests for {@link Seeker}.
 *
 * <p>
 * Design your tests on paper first (equivalence partitions, boundary values,
 * decision table)
 * from {@code docs/REQUIREMENTS.md} FR-1.1 / FR-1.3 / FR-1.2, then implement
 * them here. One
 * worked example is provided; the {@code TODO}s are yours.
 * </p>
 */
class SeekerSpecBasedTest {

    // ---- Worked example: boundary value at the maximum single top-up (FR-1.3)
    // ----
    @Test
    @DisplayName("Top-up exactly at the 5000 SEK single-transaction maximum is accepted")
    void topUpAtSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("sam@example.com", "Sam", "0707654321");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP); // 5000.00, the boundary value

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_SINGLE_TOP_UP);
    }

    // TODO (EP): one valid + one invalid equivalence class for email, name, and
    // phone (FR-1.1).
    // TODO (BVA): just-below / at / just-above the 10.00 minimum top-up (FR-1.3).
    // TODO (BVA): a top-up that would push the balance above 20000.00 is rejected
    // (FR-1.3).
    // TODO (Decision table): expected fee + max-bookings for each trust tier
    // (FR-1.2).

    @Test
    @DisplayName("Adding 250 SEK to a new seeker gives a 250.00 balance")
    void addingFundsWorks() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567"); // Arrange
        seeker.addFunds(250.00); // Act // Act
        assertThat(seeker.getBalance()).isEqualTo(250.00); // Assert
    }

    @Test
    @DisplayName("Valid email is accepted at registration")
    void validEmailIsAccepted() {
        Seeker seeker = new Seeker("exampel@exampel.com", "Sam", "0707654321");
        assertThat(seeker.getEmail()).isEqualTo("exampel@exampel.com");
    }

    @Test
    @DisplayName("Invalid email is rejected at registration")
    void invalidEmailIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("not-an-email", "Sam", "0707654321"));
    }

    @Test
    @DisplayName("Valid name is accepted at registration")
    void validNameIsAccepted() {
        Seeker seeker = new Seeker("exampel@exampel.com", "ThisIsCorrect", "0707654321");
        assertThat(seeker.getDisplayName()).isEqualTo("ThisIsCorrect");
    }

    @Test
    @DisplayName("Invalid name is rejected at registration")
    void invalidNameIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("exampel@exampel.com", ".:Thï´s îs not côrrect!?:♂.", "0707654321"));
    }

    @Test
    @DisplayName("Valid phone number is accepted at registration")
    void validPhoneNumberIsAccepted() {
        Seeker seeker = new Seeker("exampel@exampel.com", "ThisIsCorrect", "0701112223");
        assertThat(seeker.getPhoneNumber()).isEqualTo("0701112223");
    }

    @Test
    @DisplayName("Invalid phone number is rejected at registration")
    void invalidPhoneNumberIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("exampel@exampel.com", "ThisIsCorrect", "07045672198436"));
    }

    @Test
    @DisplayName("Deposit causing wallet to exceed 20000 is rejected")
    void addingFundsToFullWallet() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);

        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(seeker.MIN_TOP_UP));
        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_BALANCE);
    }

    // 2.2 Below

    @Test
    @DisplayName("Adding 5000.01 SEK (above the 5000 single max) is rejected")
    void addingFundsToBig() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567"); // Arrange
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(seeker.MAX_SINGLE_TOP_UP + 0.01)); // Act
        assertThat(seeker.getBalance()).isEqualTo(0.00); // Assert
    }

    @Test
    @DisplayName("Adding 5000.00 SEK is accepted")
    void addExactMaxLimitToWallet() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_SINGLE_TOP_UP);
    }

    @Test
    @DisplayName("Adding 4999.99 SEK is accepted")
    void addJustBelowMaxLimitToWallet() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP - 0.01);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_SINGLE_TOP_UP - 0.01);
    }

    @Test
    @DisplayName("Adding 9.99 SEK (below the 10 single min) is rejected")
    void addingFundsToSmall() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567"); // Arrange
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(seeker.MIN_TOP_UP - 0.01)); // Act
        assertThat(seeker.getBalance()).isEqualTo(0.00); // Assert
    }

    @Test
    @DisplayName("Adding 10.00 SEK is accepted")
    void addExactMinimumLimitToWallet() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MIN_TOP_UP);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MIN_TOP_UP);
    }

    @Test
    @DisplayName("Adding 10.01 SEK is accepted")
    void addJustAboveMinimumLimitToWallet() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MIN_TOP_UP + 0.01);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MIN_TOP_UP + 0.01);
    }

    @Test
    @DisplayName("Deposit causing wallet to exceed 20000.00 is rejected")
    void depositingWhenWalletIsFull() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP - 10);

        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(seeker.MIN_TOP_UP + 0.01));
        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_BALANCE - 10.00);
    }

    @Test
    @DisplayName("Deposit causing wallet to contain exactly 20000.00 is accepted")
    void fillingWalletToExactMaximum() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_BALANCE);
    }

    @Test
    @DisplayName("Deposit causing wallet to contain just below 20000.00 is accepted")
    void fillingWalletToJustBelowMaximum() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP);
        seeker.addFunds(seeker.MAX_SINGLE_TOP_UP - 0.01);

        assertThat(seeker.getBalance()).isEqualTo(seeker.MAX_BALANCE - 0.01);
    }

    // 2.3 Below

    @Test
    @DisplayName("NEW trust tier has correct max concurrent bookings and platform fee")
    void newTrustTierIsCorrect() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");

        seeker.setTrustTier(TrustTier.NEW);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(1);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("VERIFIED trust tier has correct max concurrent bookings and platform fee")
    void verifiedTrustTierIsCorrect() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");

        seeker.setTrustTier(TrustTier.VERIFIED);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(3);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.12);
    }

    @Test
    @DisplayName("TRUSTED trust tier has correct max concurrent bookings and platform fee")
    void trustedTrustTierIsCorrect() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");

        seeker.setTrustTier(TrustTier.TRUSTED);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(5);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.08);
    }

    @Test
    @DisplayName("PRO_SITTER trust tier has correct max concurrent bookings and platform fee")
    void proSitterTrustTierIsCorrect() {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");

        seeker.setTrustTier(TrustTier.PRO_SITTER);

        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(10);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.05);
    }

    @Test
    @DisplayName("Test all tier limits")
    void testAllTierLimits() {
        newTrustTierIsCorrect();
        proSitterTrustTierIsCorrect();
        verifiedTrustTierIsCorrect();
        proSitterTrustTierIsCorrect();
    }

    @ParameterizedTest(name = "Decision table: {0} gives max {1} bookings and fee {2}")
    @CsvSource({
            "NEW,        1,  0.15",
            "VERIFIED,   3,  0.12",
            "TRUSTED,    5,  0.08",
            "PRO_SITTER, 10, 0.05"

    })
    void tierLimitsMatchDecisionTable(TrustTier tier, int maxBookings, double fee) {
        Seeker seeker = new Seeker("you@example.com", "You", "0701234567");
        seeker.setTrustTier(tier);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(maxBookings);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(fee);
    }

}
