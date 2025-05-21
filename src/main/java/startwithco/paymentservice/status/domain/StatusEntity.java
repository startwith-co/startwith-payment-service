package startwithco.paymentservice.status.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import startwithco.paymentservice.base.BaseTimeEntity;
import startwithco.paymentservice.payment.domain.PaymentEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "STATUS_ENTITY")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@SuperBuilder
public class StatusEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_seq")
    private Long statusSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_seq", nullable = false)
    private PaymentEntity paymentEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumer_status", nullable = false)
    private CONSUMER_STATUS consumerStatus;

    public enum CONSUMER_STATUS {
        DEVELOPING,       // 개발 진행 중 (결제 완료 시)
        DEVELOPED,        // 개발 완료 (개발 완료 알람 시)
        CONFIRMED,        // 구매 확정 (개발 완료 알람 1-2주 후)
        CANCELED          // 결제 취소
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_status", nullable = false)
    private VENDOR_STATUS vendorStatus;

    public enum VENDOR_STATUS {
        WAITING_CHAT,     // 실시간 상담 대기
        CONFIRMED_WAIT,   // 구매 확정 대기 (개발 완료 알람 시)
        CONFIRMED,        // 구매 확정 (개발 완료 알람 1~2주 후)
        CANCELED,         // 결제 취소
        SETTLEMENT_WAIT,  // 정산 대기 (개발 완료 알람 시)
        SETTLED,          // 정산 완료 (개발 완료 알람 1-2주 후)
        REFUND_REQUESTED  // 환불 요청
    }

    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    @Column(name = "development_completed_at")
    private LocalDateTime developmentCompletedAt;

    @Column(name = "actual_duration")
    private Long actualDuration;
}
