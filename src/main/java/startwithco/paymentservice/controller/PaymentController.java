package startwithco.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import startwithco.paymentservice.base.BaseResponse;
import startwithco.paymentservice.exception.badRequest.BadRequestErrorResult;
import startwithco.paymentservice.exception.badRequest.BadRequestException;
import startwithco.paymentservice.service.PaymentService;

import static startwithco.paymentservice.dto.TossPaymentResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-service")
public class PaymentController {
    private final PaymentService service;

    @GetMapping("/toss-payment-approval")
    public Mono<ResponseEntity<BaseResponse<TossPaymentApprovalResponseDto>>> tossPaymentApproval(
            @RequestParam(name = "paymentKey") String paymentKey,
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "amount") Long amount
    ) {
        if (paymentKey == null || paymentKey.isEmpty() || orderId == null || orderId.isEmpty() || amount == null) {
            throw new BadRequestException(BadRequestErrorResult.BAD_REQUEST_EXCEPTION);
        }

        return service.tossPaymentApproval(paymentKey, orderId, amount)
                .map(result -> ResponseEntity.ok(BaseResponse.ofSuccess(HttpStatus.OK.value(), result)));
    }
}
