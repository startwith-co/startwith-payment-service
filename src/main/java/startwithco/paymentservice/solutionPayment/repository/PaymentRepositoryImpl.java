package startwithco.paymentservice.solutionPayment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.solutionPayment.domain.PaymentEntity;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentEntityJpaRepository repository;

    @Override
    public PaymentEntity save(PaymentEntity payment) {
        return repository.save(payment);
    }
}
