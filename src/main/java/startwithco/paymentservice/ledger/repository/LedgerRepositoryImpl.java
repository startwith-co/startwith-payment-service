package startwithco.paymentservice.ledger.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import startwithco.paymentservice.ledger.domain.LedgerEntity;

@Repository
@RequiredArgsConstructor
public class LedgerRepositoryImpl implements LedgerRepository {
    private final LedgerEntityJpaRepository repository;

    @Override
    public LedgerEntity save(LedgerEntity ledgerEntity) {
        return repository.save(ledgerEntity);
    }
}
