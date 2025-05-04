package startwithco.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import startwithco.paymentservice.exception.badRequest.BadRequestErrorResult;
import startwithco.paymentservice.exception.badRequest.BadRequestException;
import startwithco.paymentservice.exception.server.ServerErrorResult;
import startwithco.paymentservice.exception.server.ServerException;
import startwithco.paymentservice.domain.PaymentEntity;
import startwithco.paymentservice.executor.TossPaymentApprovalExecutor;
import startwithco.paymentservice.repository.PaymentRepository;

import java.util.Map;

import static startwithco.paymentservice.dto.TossPaymentResponseDto.*;
import static startwithco.paymentservice.topic.consumer.ConsumerTopic.SOLUTION_PAYMENT_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository repository;

    private final TossPaymentApprovalExecutor tossPaymentApprovalExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @KafkaListener(topics = SOLUTION_PAYMENT_TOPIC, groupId = "group-01")
    public void savePaymentEntity(String event) {
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
            throw new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION);
        }
    }

    @Transactional
    public Mono<TossPaymentApprovalResponseDto> tossPaymentApproval(String paymentKey, String orderId, Long amount) {
        PaymentEntity result = repository.findByOrderId(orderId);

        if (result.getAmount() != amount) {
            throw new BadRequestException(BadRequestErrorResult.AMOUNT_MISMATCH_BAD_REQUEST_EXCEPTION);
        }

        return tossPaymentApprovalExecutor.executeApproval(paymentKey, orderId, amount);
    }
}
