package startwithco.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import startwithco.paymentservice.exception.server.ServerErrorResult;
import startwithco.paymentservice.exception.server.ServerException;
import startwithco.paymentservice.domain.PaymentEntity;
import startwithco.paymentservice.repository.PaymentRepository;

import java.util.Map;

import static startwithco.paymentservice.topic.consumer.ConsumerTopic.SOLUTION_PAYMENT_TOPIC;
import static startwithco.paymentservice.topic.producer.ProducerTopic.PAYMENT_SOLUTION_ROLLBACK_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository repository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @KafkaListener(topics = SOLUTION_PAYMENT_TOPIC, groupId = "group-01")
    public void savePaymentEntity(String event) throws JsonProcessingException {
        try {
            Map<String, Object> payload = objectMapper.readValue(event, Map.class);
            Long solutionSeq = ((Number) payload.get("solutionSeq")).longValue();
            Long amount = ((Number) payload.get("amount")).longValue();
            String orderId = (String) payload.get("orderId");
            String orderName = (String) payload.get("orderName");

            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .solutionSeq(solutionSeq)
                    .amount(amount)
                    .orderId(orderId)
                    .orderName(orderName)
                    .build();

            repository.save(paymentEntity);
        } catch (Exception e) {
            Map<String, Object> payload = objectMapper.readValue(event, Map.class);
            Long solutionSeq = ((Number) payload.get("solutionSeq")).longValue();

            kafkaTemplate.send(PAYMENT_SOLUTION_ROLLBACK_TOPIC, String.valueOf(solutionSeq));

            throw new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION);
        }
    }
}
