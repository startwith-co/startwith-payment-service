package startwithco.paymentservice.payment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import startwithco.paymentservice.base.BaseTimeEntity;
import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;

@Entity
@Table(name = "PAYMENT_ENTITY")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@SuperBuilder
public class PaymentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_seq")
    private Long paymentSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_event_seq", nullable = false)
    private PaymentEventEntity paymentEventEntity;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey;

    @Column(name = "status", nullable = false)
    private STATUS status;

    public enum STATUS {
        READY,        // 결제 준비
        EXECUTED,     // 결제 승인 요청됨
        COMPLETED,    // 결제 완료
        CANCELED,     // 사용자에 의해 취소
        FAILURE       // 결제 실패
    }
}
