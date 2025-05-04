package startwithco.paymentservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.domain.PaymentEntity;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentEntityJpaRepository repository;

    @Override
    public PaymentEntity save(PaymentEntity payment) {
        return repository.save(payment);
    }
}
