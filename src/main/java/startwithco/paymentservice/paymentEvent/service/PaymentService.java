package startwithco.paymentservice.paymentEvent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import startwithco.paymentservice.exception.badRequest.BadRequestErrorResult;
import startwithco.paymentservice.exception.badRequest.BadRequestException;
import startwithco.paymentservice.exception.server.ServerErrorResult;
import startwithco.paymentservice.exception.server.ServerException;
import startwithco.paymentservice.executor.TossPaymentApprovalExecutor;
import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;
import startwithco.paymentservice.paymentEvent.repository.PaymentRepository;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderStatus;
import startwithco.paymentservice.paymentOrder.repository.PaymentOrderRepository;

import java.util.Map;

import static startwithco.paymentservice.paymentEvent.dto.TossPaymentResponseDto.TossPaymentApprovalResponseDto;
import static startwithco.paymentservice.topic.ConsumerTopic.TOSS_PAYMENT_QUERY_TOPIC;
import static startwithco.paymentservice.topic.ProducerTopic.TOSS_PAYMENT_APPROVAL_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    private final TossPaymentApprovalExecutor tossPaymentApprovalExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @KafkaListener(topics = TOSS_PAYMENT_QUERY_TOPIC, groupId = "group-01")
    public void savePaymentEntity(String event) {
        try {
            Map<String, Object> payload = objectMapper.readValue(event, Map.class);
            Long solutionSeq = ((Number) payload.get("solutionSeq")).longValue();
            Long buyerSeq = ((Number) payload.get("buyerSeq")).longValue();
            Long sellerSeq = ((Number) payload.get("sellerSeq")).longValue();
            Long amount = ((Number) payload.get("amount")).longValue();
            String orderId = (String) payload.get("orderId");
            String orderName = (String) payload.get("orderName");

            PaymentEventEntity paymentEventEntity = paymentRepository.save(
                    PaymentEventEntity.builder()
                            .solutionSeq(solutionSeq)
                            .amount(amount)
                            .orderId(orderId)
                            .orderName(orderName)
                            .build()
            );

            paymentOrderRepository.save(
                    PaymentOrderEntity.builder()
                            .paymentEventSeq(paymentEventEntity.getPaymentEventSeq())
                            .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                            .amount(amount)
                            .buyerSeq(buyerSeq)
                            .sellerSeq(sellerSeq)
                            .build()
            );

        } catch (Exception e) {
            /*
             * TODO
             * solution-service는 제대로 Response 된 경우
             * PaymentEvent 저장 시에 롤백 처리 되면 어떻게 처리 ?
             */

            throw new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION);
        }
    }

    @Transactional
    public Mono<TossPaymentApprovalResponseDto> tossPaymentApproval(String paymentKey, String orderId, Long amount) {
        PaymentEventEntity paymentEventEntity = paymentRepository.findByOrderId(orderId);
        PaymentOrderEntity paymentOrderEntity = paymentOrderRepository.findByPaymentEventSeq(paymentEventEntity.getPaymentEventSeq());

        paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.EXECUTED);
        paymentOrderRepository.save(paymentOrderEntity);

        try {
            if (!paymentEventEntity.getAmount().equals(amount)) {
                throw new BadRequestException(BadRequestErrorResult.AMOUNT_MISMATCH_BAD_REQUEST_EXCEPTION);
            }

            paymentEventEntity.updateTossPaymentApproval(paymentKey);
            paymentRepository.save(paymentEventEntity);
            paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.SUCCESS);
            paymentOrderRepository.save(paymentOrderEntity);

            kafkaTemplate.send(TOSS_PAYMENT_APPROVAL_TOPIC, String.valueOf(paymentEventEntity.getSolutionSeq()));
        } catch (Exception e) {
            paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.FAILURE);
            paymentOrderRepository.save(paymentOrderEntity);

            throw new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION);
        }

        return tossPaymentApprovalExecutor.executeApproval(paymentKey, orderId, amount);
    }
}
