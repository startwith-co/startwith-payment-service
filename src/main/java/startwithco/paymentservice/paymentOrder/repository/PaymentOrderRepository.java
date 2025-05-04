package startwithco.paymentservice.paymentOrder.repository;

import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;

import java.util.Optional;

public interface PaymentOrderRepository {
    PaymentOrderEntity save(PaymentOrderEntity paymentOrder);

    PaymentOrderEntity findByPaymentEventSeq(Long paymentEventSeq);
}
