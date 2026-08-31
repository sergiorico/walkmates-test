package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Your very first WalkMates test — the "day one" win.
 *
 * <p>These tests already pass. Run them with {@code mvn test -Dtest=BeginnerFirstTest}. Read
 * {@code FIRST_TEST_TUTORIAL.md} alongside this file, then start adding your own tests in
 * {@code SeekerSpecBasedTest}.</p>
 *
 * <p>Every test follows <strong>Arrange — Act — Assert</strong>: set things up, do the thing,
 * then check the result.</p>
 */
class BeginnerFirstTest {

    @Test
    @DisplayName("A new seeker starts at the NEW trust tier with a zero balance")
    void newSeekerStartsAtNewTierWithZeroBalance() {
        // Arrange + Act: register a seeker with valid details (FR-1.1).
        Seeker seeker = new Seeker("alex@example.com", "Alex", "0701234567");

        // Assert: the defaults from FR-1.2 / FR-1.3.
        assertThat(seeker.getTrustTier()).isEqualTo(TrustTier.NEW);
        assertThat(seeker.getBalance()).isEqualTo(0.00);
    }

    @Test
    @DisplayName("Adding valid funds increases the balance")
    void addingFundsIncreasesBalance() {
        // Arrange
        Seeker seeker = new Seeker("alex@example.com", "Alex", "0701234567");

        // Act: a top-up within the allowed range (FR-1.3).
        seeker.addFunds(100.00);

        // Assert
        assertThat(seeker.getBalance()).isEqualTo(100.00);
    }

    @Test
    @DisplayName("A top-up below the minimum is rejected")
    void topUpBelowMinimumIsRejected() {
        // Arrange
        Seeker seeker = new Seeker("alex@example.com", "Alex", "0701234567");

        // Act + Assert: 9.99 is below the 10.00 minimum (FR-1.3), so it throws.
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(9.99));
    }
}
