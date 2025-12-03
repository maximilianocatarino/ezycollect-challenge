package io.ezycollect.payment.domain.event;

import io.ezycollect.payment.application.port.command.PaymentEventInterface;
import io.ezycollect.payment.domain.model.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEvent implements PaymentEventInterface {
    public enum EventType {
        PAYMENT_SETTLED
    }

    private final EventType eventType;
    private final Payment payment;

    public PaymentEvent(EventType eventType, Payment payment) {
        this.eventType = eventType;
        this.payment = payment;
    }
}
