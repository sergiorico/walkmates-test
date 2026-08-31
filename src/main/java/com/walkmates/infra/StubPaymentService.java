package com.walkmates.infra;

import com.walkmates.service.PaymentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default {@link PaymentService} that always succeeds — lets wallet top-ups work in the demo
 * with no real gateway. In Lab 2 this seam is mocked to drive the decline and timeout paths.
 */
@Component
public class StubPaymentService implements PaymentService {

    @Override
    public String charge(String seekerId, String paymentMethodId, double amount) {
        return "STUB-" + UUID.randomUUID();
    }
}
