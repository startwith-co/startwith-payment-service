package startwithco.paymentservice.paymentEvent.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;

import java.util.Optional;

@Repository
public interface PaymentEntityJpaRepository extends JpaRepository<PaymentEventEntity, Long> {
    PaymentEventEntity save(PaymentEventEntity paymentEntity);

    @Query("select p from PaymentEventEntity p where p.orderId = :orderId")
    Optional<PaymentEventEntity> findByOrderId(@Param("orderId") String orderId);
}
