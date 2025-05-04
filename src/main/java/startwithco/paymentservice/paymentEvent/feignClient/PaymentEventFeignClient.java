package startwithco.paymentservice.paymentEvent.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static startwithco.paymentservice.paymentEvent.dto.PaymentResponseDto.*;

@FeignClient(name = "solution-service")
public interface PaymentEventFeignClient {
    @GetMapping("/api/solution-service/toss-payment-query")
    BaseResponseFeignClient<SolutionResponseFeignClientDto> getSolution(@RequestParam(name = "solutionSeq") Long solutionSeq);
}
