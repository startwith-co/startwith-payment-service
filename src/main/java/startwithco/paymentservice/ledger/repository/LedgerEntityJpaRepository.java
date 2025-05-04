package startwithco.paymentservice.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.ledger.domain.LedgerEntity;

@Repository
public interface LedgerEntityJpaRepository extends JpaRepository<LedgerEntity, Long> {
}
