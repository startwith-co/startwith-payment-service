package startwithco.paymentservice.ledger.repository;

import startwithco.paymentservice.ledger.domain.LedgerEntity;

public interface LedgerRepository {
    LedgerEntity save(LedgerEntity ledgerEntity);
}
