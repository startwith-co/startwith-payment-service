package startwithco.paymentservice.paymentOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;

import java.util.Optional;

@Repository
public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, Long> {
    @Query("select po from PaymentOrderEntity po where po.paymentEventSeq = :paymentEventSeq")
    Optional<PaymentOrderEntity> findByPaymentEventSeq(Long paymentEventSeq);
}
