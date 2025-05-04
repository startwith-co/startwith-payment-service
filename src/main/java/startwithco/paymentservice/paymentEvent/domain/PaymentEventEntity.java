package startwithco.paymentservice.paymentEvent.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import startwithco.paymentservice.base.BaseTimeEntity;

@Entity
@Table(name = "PAYMENT")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
public class PaymentEventEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_event_seq")
    private Long paymentEventSeq;

    @Column(name = "solution_seq", nullable = false)
    private Long solutionSeq;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "payment_key", nullable = true)
    private String paymentKey;

    @Column(name = "is_payment_done", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPaymentDone = false;

    public void updateTossPaymentApproval(String paymentKey) {
        isPaymentDone = true;
        this.paymentKey = paymentKey;
    }
}
