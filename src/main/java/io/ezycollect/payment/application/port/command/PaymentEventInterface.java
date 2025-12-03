package io.ezycollect.payment.application.port.command;

import io.ezycollect.payment.domain.model.Payment;

public interface PaymentEventInterface {
    Payment getPayment();
}
