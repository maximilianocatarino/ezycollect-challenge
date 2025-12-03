package io.ezycollect.payment.application.port.query;

import io.ezycollect.payment.domain.model.Payment;

public interface PaymentRequestCommandQuery {
    Payment findById(String id);
}
