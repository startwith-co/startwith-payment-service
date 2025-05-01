package startwithco.paymentservice.solutionPayment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static startwithco.paymentservice.solutionPayment.kafka.topic.consumer.ConsumerTopic.SOLUTION_PAYMENT_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    @Transactional
    @KafkaListener(topics = SOLUTION_PAYMENT_TOPIC, groupId = "group-01")
    public void savePaymentEntity(String event) {
        try {
            log.info("Received payment event: {}", event);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
