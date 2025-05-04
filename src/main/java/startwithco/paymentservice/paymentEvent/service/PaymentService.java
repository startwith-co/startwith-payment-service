package startwithco.paymentservice.paymentEvent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import startwithco.paymentservice.exception.badRequest.BadRequestErrorResult;
import startwithco.paymentservice.exception.badRequest.BadRequestException;
import startwithco.paymentservice.exception.server.ServerErrorResult;
import startwithco.paymentservice.exception.server.ServerException;
import startwithco.paymentservice.executor.TossPaymentApprovalExecutor;
import startwithco.paymentservice.ledger.domain.LedgerEntity;
import startwithco.paymentservice.ledger.repository.LedgerRepository;
import startwithco.paymentservice.paymentEvent.domain.PaymentEventEntity;
import startwithco.paymentservice.paymentEvent.feignClient.PaymentEventFeignClient;
import startwithco.paymentservice.paymentEvent.repository.PaymentRepository;
import startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity;
import startwithco.paymentservice.paymentOrder.repository.PaymentOrderRepository;

import java.util.UUID;

import static startwithco.paymentservice.ledger.domain.LedgerEntity.*;
import static startwithco.paymentservice.paymentEvent.dto.PaymentResponseDto.*;
import static startwithco.paymentservice.paymentEvent.dto.PaymentResponseDto.TossPaymentApprovalResponseDto;
import static startwithco.paymentservice.paymentOrder.domain.PaymentOrderEntity.*;
import static startwithco.paymentservice.topic.ProducerTopic.TOSS_PAYMENT_APPROVAL_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final LedgerRepository ledgerRepository;

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

        // 결제 승인 로직 진입 시 EXECUTING 상태로 변경
        paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.EXECUTED);
        paymentOrderRepository.save(paymentOrderEntity);

        // 금액 불일치 시 바로 예외 처리
        if (!paymentEventEntity.getAmount().equals(amount)) {
            paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.FAILURE);
            paymentOrderRepository.save(paymentOrderEntity);

            throw new BadRequestException(BadRequestErrorResult.AMOUNT_MISMATCH_BAD_REQUEST_EXCEPTION);
        }

        return tossPaymentApprovalExecutor.executeApproval(paymentKey, orderId, amount)
                .flatMap(response -> {
                    // 결제 승인 성공 처리
                    paymentEventEntity.updateTossPaymentApproval(paymentKey);
                    paymentRepository.save(paymentEventEntity);

                    // 이중 부기 방식으로 ledger에 기록
                    ledgerRepository.save(LedgerEntity.builder()
                            .entryType(EntryType.DEBIT)
                            .orderId(orderId)
                            .debit(amount)
                            .credit(0L)
                            .build());

                    ledgerRepository.save(LedgerEntity.builder()
                            .entryType(EntryType.CREDIT)
                            .orderId(orderId)
                            .debit(0L)
                            .credit(amount)
                            .build());

                    // 상태 SUCCESS로 변경
                    paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderEntity.updateLedgerUpdated();
                    paymentOrderRepository.save(paymentOrderEntity);

                    // 결제 승인 성공 메시지 발행
                    kafkaTemplate.send(TOSS_PAYMENT_APPROVAL_TOPIC, String.valueOf(paymentEventEntity.getSolutionSeq()));

                    return Mono.just(response);
                })
                .onErrorResume(ex -> {
                    // 예외 유형에 따라 상태 처리
                    if (ex instanceof WebClientResponseException) {
                        // PG사 응답 오류 (ex: 잔액 부족, 유효하지 않은 카드 정보 등)
                        paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.FAILURE);
                    } else {
                        // 네트워크 오류 등 예기치 못한 예외
                        paymentOrderEntity.updatePaymentOrderStatus(PaymentOrderStatus.UNKNOWN);
                    }

                    paymentOrderRepository.save(paymentOrderEntity);
                    return Mono.error(new ServerException(ServerErrorResult.INTERNAL_SERVER_EXCEPTION));
                });
    }
}
