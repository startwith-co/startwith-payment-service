package startwithco.paymentservice.solutionPayment.repository;

import startwithco.paymentservice.solutionPayment.domain.PaymentEntity;

public interface PaymentRepository {
    PaymentEntity save(PaymentEntity payment);
}
