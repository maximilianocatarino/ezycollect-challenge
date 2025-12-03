package io.ezycollect.payment.infra.database.adapter;

import io.ezycollect.payment.infra.database.PaymentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentDocument, String> {
}