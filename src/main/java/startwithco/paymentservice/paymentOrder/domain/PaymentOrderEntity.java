package startwithco.paymentservice.paymentOrder.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "PAYMENT_ORDER")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
public class PaymentOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_order_seq")
    private Long paymentOrderSeq;

    @Column(name = "payment_event_seq", nullable = false)
    private Long paymentEventSeq;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_order_status", nullable = false)
    private PaymentOrderStatus paymentOrderStatus;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "ledger_updated", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean ledgerUpdated = false;

    @Column(name = "wallet_updated", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean walletUpdated = false;

    @Column(name = "buyer_seq", nullable = false)
    private Long buyerSeq;

    @Column(name = "seller_seq", nullable = false)
    private Long sellerSeq;

    public void updatePaymentOrderStatus(PaymentOrderStatus paymentOrderStatus) {
        this.paymentOrderStatus = paymentOrderStatus;
    }
}
