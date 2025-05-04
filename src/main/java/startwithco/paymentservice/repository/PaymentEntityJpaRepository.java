package startwithco.paymentservice.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.domain.PaymentEntity;

import java.util.Optional;

@Repository
public interface PaymentEntityJpaRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity save(PaymentEntity paymentEntity);

    @Query("select p from PaymentEntity p where p.orderId = :orderId")
    Optional<PaymentEntity> findByOrderId(@Param("orderId") String orderId);
}
