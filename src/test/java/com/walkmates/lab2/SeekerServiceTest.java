package com.walkmates.lab2;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PaymentService;
import com.walkmates.service.SeekerService;

class SeekerServiceTest {

    @Test
    void topUpCreditsWalletAfterSuccessfulCharge() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers,
                payments,
                notifications
        );

        Seeker seeker = new Seeker(
                "test@example.com",
                "Test Seeker",
                "0701234567"
        );

        when(seekers.findById("seeker-1"))
                .thenReturn(Optional.of(seeker));

        when(payments.charge("seeker-1", "card-1", 250.00))
                .thenReturn("transaction-1");

        when(seekers.save(seeker))
                .thenReturn(seeker);

        Seeker result = service.topUp(
                "seeker-1",
                "card-1",
                250.00
        );

        assertThat(result.getBalance()).isEqualTo(250.00);

        verify(payments).charge(
                "seeker-1",
                "card-1",
                250.00
        );

        verify(seekers).save(seeker);
    }

    @Test
    void declinedChargeDoesNotCreditWallet() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers,
                payments,
                notifications
        );

        Seeker seeker = new Seeker(
                "test@example.com",
                "Test Seeker",
                "0701234567"
        );

        when(seekers.findById("seeker-1"))
                .thenReturn(Optional.of(seeker));

        when(payments.charge("seeker-1", "card-1", 250.00))
                .thenThrow(new PaymentService.PaymentException("declined"));

        assertThatThrownBy(
                () -> service.topUp(
                        "seeker-1",
                        "card-1",
                        250.00
                )
        )
                .isInstanceOf(PaymentService.PaymentException.class);

        assertThat(seeker.getBalance()).isZero();

        verify(seekers, never()).save(any());
    }

    @Test
    void paymentTimeoutDoesNotCreditWallet() throws PaymentService.PaymentException {
        SeekerRepository seekers = mock(SeekerRepository.class);
        PaymentService payments = mock(PaymentService.class);
        NotificationService notifications = mock(NotificationService.class);

        SeekerService service = new SeekerService(
                seekers,
                payments,
                notifications
        );

        Seeker seeker = new Seeker(
                "test@example.com",
                "Test Seeker",
                "0701234567"
        );

        when(seekers.findById("seeker-1"))
                .thenReturn(Optional.of(seeker));

        when(payments.charge("seeker-1", "card-1", 250.00))
                .thenThrow(new PaymentService.PaymentTimeoutException("timeout"));

        assertThatThrownBy(
                () -> service.topUp(
                        "seeker-1",
                        "card-1",
                        250.00
                )
        )
                .isInstanceOf(PaymentService.PaymentTimeoutException.class);

        assertThat(seeker.getBalance()).isZero();

        verify(seekers, never()).save(any());
    }
}