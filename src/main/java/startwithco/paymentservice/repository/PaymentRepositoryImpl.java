package startwithco.paymentservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.domain.PaymentEntity;
import startwithco.paymentservice.exception.notFound.NotFoundErrorResult;
import startwithco.paymentservice.exception.notFound.NotFoundException;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentEntityJpaRepository repository;

    @Override
    public PaymentEntity save(PaymentEntity payment) {
        return repository.save(payment);
    }

    @Override
    public PaymentEntity findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorResult.ORDER_ID_NOT_FOUND_EXCEPTION));
    }
}
