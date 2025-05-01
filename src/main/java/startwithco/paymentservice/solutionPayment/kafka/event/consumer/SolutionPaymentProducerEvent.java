package startwithco.paymentservice.solutionPayment.kafka.event.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionPaymentProducerEvent {
    private Long amount;
    private String orderId;
    private String orderName;
}
