package startwithco.paymentservice.paymentOrder.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.exception.notFound.NotFoundErrorResult;
import startwithco.paymentservice.exception.notFound.NotFoundException;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;

import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {
    private final PaymentOrderJpaRepository repository;

    @Override
    public PaymentOrderEntity save(PaymentOrderEntity paymentOrder) {
        return repository.save(paymentOrder);
    }

    @Override
    public PaymentOrderEntity findByPaymentEventSeq(Long paymentEventSeq) {
        return repository.findByPaymentEventSeq(paymentEventSeq)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorResult.PAYMENT_EVENT_NOT_FOUND_EXCEPTION));
    }
}
