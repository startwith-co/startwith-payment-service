package startwithco.paymentservice.repository;

import startwithco.paymentservice.domain.PaymentEntity;

public interface PaymentRepository {
    PaymentEntity save(PaymentEntity payment);

    PaymentEntity findByOrderId(String orderId);
}
