package startwithco.paymentservice.paymentEvent.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;
import startwithco.paymentservice.exception.notFound.NotFoundErrorResult;
import startwithco.paymentservice.exception.notFound.NotFoundException;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentEntityJpaRepository repository;

    @Override
    public PaymentEventEntity save(PaymentEventEntity payment) {
        return repository.save(payment);
    }

    @Override
    public PaymentEventEntity saveAndFlush(PaymentEventEntity payment) {
        return repository.saveAndFlush(payment);
    }

    @Override
    public PaymentEventEntity findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorResult.ORDER_ID_NOT_FOUND_EXCEPTION));
    }
}
