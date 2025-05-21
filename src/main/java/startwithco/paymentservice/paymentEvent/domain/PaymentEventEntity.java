package startwithco.paymentservice.paymentEvent.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import startwithco.paymentservice.base.BaseTimeEntity;

@Entity
@Table(name = "PAYMENT_EVENT_ENTITY")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@SuperBuilder
public class PaymentEventEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_event_seq")
    private Long paymentEventSeq;

    @Column(name = "vendor_seq", nullable = false)
    private Long vendorSeq;

    @Column(name = "consumer_seq", nullable = false)
    private Long consumerSeq;

    @Column(name = "solution_seq", nullable = false)
    private Long solutionSeq;

    @Column(name = "solution_name", nullable = false)
    private String solutionName;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "sell_type", nullable = false)
    private SELL_TYPE sellType;

    public enum SELL_TYPE {
        SINGLE,
        SUBSCRIBE
    }

    @Column(name = "duration", nullable = false)
    private Long duration;
}
