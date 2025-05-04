package startwithco.paymentservice.ledger.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import startwithco.paymentservice.base.BaseTimeEntity;

@Entity
@Table(name = "LEDGER")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
public class LedgerEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_seq")
    private Long ledgerSeq;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    @Column(name = "debit", nullable = false, updatable = false)
    private Long debit;

    @Column(name = "credit", nullable = false, updatable = false)
    private Long credit;

    public enum EntryType {
        DEBIT,
        CREDIT
    }
}
