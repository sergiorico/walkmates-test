package com.walkmates.infra;

import com.walkmates.model.Booking;
import com.walkmates.model.Seeker;
import com.walkmates.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default {@link NotificationService} that just logs. Real delivery (email/SMS) would replace
 * this. In tests this seam is mocked so interactions can be verified.
 */
@Component
public class LoggingNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    public void sendBookingConfirmed(Seeker seeker, Booking booking) {
        log.info("Booking {} confirmed for seeker {}", booking.getId(), seeker.getId());
    }

    @Override
    public void sendBookingCancelled(Seeker seeker, Booking booking) {
        log.info("Booking {} cancelled for seeker {}", booking.getId(), seeker.getId());
    }

    @Override
    public void sendLowBalanceWarning(Seeker seeker, double currentBalance) {
        log.info("Low balance ({}) warning for seeker {}", currentBalance, seeker.getId());
    }
}
