package startwithco.paymentservice.exception.notFound;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotFoundErrorResult {
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "NOT FOUND EXCEPTION", "NFE001"),
    SOLUTION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "SOLUTION NOT FOUND EXCEPTION", "SNFE002"),
    ORDER_ID_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "ORDER ID NOT FOUND", "OINFE003"),
    ;

    private final int httpStatus;
    private final String message;
    private final String code;
}
