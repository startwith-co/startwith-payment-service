package startwithco.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.domain.PaymentEntity;

@Repository
public interface PaymentEntityJpaRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity save(PaymentEntity paymentEntity);
}
