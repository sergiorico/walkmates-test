package com.walkmates.service;

import com.walkmates.model.Booking;
import com.walkmates.model.Seeker;

/**
 * Sends notifications to Seekers — a <strong>mockable seam</strong> for Lab 2.
 *
 * <p>In tests this is typically a Mockito mock so you can verify (or stub) interactions without
 * sending anything real. {@link BookingService} calls it on the happy path, which lets students
 * practise {@code verify(...)} and interaction testing.</p>
 */
public interface NotificationService {

    /** Notifies the Seeker that their booking was confirmed. */
    void sendBookingConfirmed(Seeker seeker, Booking booking);

    /** Notifies the Seeker that their booking was cancelled. */
    void sendBookingCancelled(Seeker seeker, Booking booking);

    /** Warns the Seeker that their wallet balance is running low. */
    void sendLowBalanceWarning(Seeker seeker, double currentBalance);
}
