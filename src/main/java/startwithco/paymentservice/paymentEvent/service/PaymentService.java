package startwithco.paymentservice.paymentEvent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import startwithco.paymentservice.paymentEvent.feignClient.PaymentEventFeignClient;
import startwithco.paymentservice.paymentEvent.repository.PaymentRepository;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderStatus;
import startwithco.paymentservice.paymentOrder.repository.PaymentOrderRepository;

import java.util.UUID;

import static startwithco.paymentservice.paymentEvent.dto.PaymentResponseDto.*;
import static startwithco.paymentservice.paymentEvent.dto.PaymentResponseDto.TossPaymentApprovalResponseDto;
import static startwithco.paymentservice.topic.ProducerTopic.TOSS_PAYMENT_APPROVAL_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    private final PaymentEventFeignClient paymentEventFeignClient;

    private final TossPaymentApprovalExecutor tossPaymentApprovalExecutor;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public TossPaymentQueryResponseDto getTossPaymentQuery(Long solutionSeq, Long buyerSeq, Long sellerSeq) {
        /*
         * TODO
         *  FeignClient 예외처리 어떻게?
         *  solution-service는 404 -> payment-service는 500 문제
         * */
        SolutionResponseFeignClientDto solutionDto = paymentEventFeignClient.getSolution(solutionSeq).data();
        String orderId = UUID.randomUUID().toString();

        PaymentEventEntity paymentEventEntity = paymentRepository.save(
                PaymentEventEntity.builder()
                        .solutionSeq(solutionSeq)
                        .amount(solutionDto.amount())
                        .orderId(orderId)
                        .orderName(solutionDto.solutionName())
                        .build()
        );

        paymentOrderRepository.save(
                PaymentOrderEntity.builder()
                        .paymentEventSeq(paymentEventEntity.getPaymentEventSeq())
                        .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                        .amount(solutionDto.amount())
                        .buyerSeq(buyerSeq)
                        .sellerSeq(sellerSeq)
                        .build()
        );

        return new TossPaymentQueryResponseDto(solutionDto.amount(), orderId, solutionDto.solutionName());
    }

    @Transactional
    public Mono<TossPaymentApprovalResponseDto> tossPaymentApproval(String paymentKey, String orderId, Long amount) {
        /*
         * TODO
         *  테스트 필요
         * */
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
            /*
             * TODO
             *  승인되지 않았을 경우에 어떻게 처리?
             * */
            paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.FAILURE);
            paymentOrderRepository.saveAndFlush(paymentOrderEntity);

            throw new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION);
        }

        return tossPaymentApprovalExecutor.executeApproval(paymentKey, orderId, amount);
    }
}
