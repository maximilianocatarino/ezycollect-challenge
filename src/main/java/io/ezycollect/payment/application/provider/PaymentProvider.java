package io.ezycollect.payment.application.provider;

import io.ezycollect.payment.domain.model.Payment;

public interface PaymentProvider {
    Payment save(Payment payment);
}
