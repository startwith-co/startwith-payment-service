package startwithco.paymentservice.paymentEvent.repository;

import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;

public interface PaymentRepository {
    PaymentEventEntity save(PaymentEventEntity payment);

    PaymentEventEntity saveAndFlush(PaymentEventEntity payment);

    PaymentEventEntity findByOrderId(String orderId);
}
