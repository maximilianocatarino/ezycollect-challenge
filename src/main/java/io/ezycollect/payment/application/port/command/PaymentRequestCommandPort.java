package io.ezycollect.payment.application.port.command;

import io.ezycollect.payment.application.port.command.dto.PaymentRequestDTO;
import io.ezycollect.payment.domain.model.Payment;

public interface PaymentRequestCommandPort {
    Payment newPayment(PaymentRequestDTO dto);
}
