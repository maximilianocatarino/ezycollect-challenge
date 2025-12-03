package io.ezycollect.payment.infra.database.adapter;

import io.ezycollect.payment.application.provider.PaymentProvider;
import io.ezycollect.payment.domain.model.Payment;
import io.ezycollect.payment.infra.database.PaymentDocument;

public class PaymentDocumentAdapter implements PaymentProvider {
    private final PaymentRepository repository;

    public PaymentDocumentAdapter(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentDocument document = PaymentDocument.fromDomain(payment);
        PaymentDocument savedDocument = repository.save(document);
        return savedDocument.toDomain();
    }
}
