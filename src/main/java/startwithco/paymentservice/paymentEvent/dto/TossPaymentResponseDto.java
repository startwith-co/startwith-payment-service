package startwithco.paymentservice.paymentEvent.dto;

public class TossPaymentResponseDto {
    public record TossPaymentApprovalResponseDto(
            String mId,
            String version,
            String paymentKey,
            String status,
            String lastTransactionKey,
            String orderId,
            String orderName,
            String requestedAt,
            String approvedAt,
            boolean useEscrow,
            boolean cultureExpense,
            Card card,
            Object virtualAccount,
            Object transfer,
            Object mobilePhone,
            Object giftCertificate,
            Object cashReceipt,
            Object cashReceipts,
            Object discount,
            Object cancels,
            Object secret,
            String type,
            Object easyPay,
            String country,
            Object failure,
            boolean isPartialCancelable,
            Receipt receipt,
            Checkout checkout,
            String currency,
            int totalAmount,
            int balanceAmount,
            int suppliedAmount,
            int vat,
            int taxFreeAmount,
            Object metadata,
            int taxExemptionAmount,
            String method
    ) {
    }

    public record Card(
            String issuerCode,
            String acquirerCode,
            String number,
            int installmentPlanMonths,
            boolean isInterestFree,
            Object interestPayer,
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            int amount
    ) {
    }

    public record Receipt(
            String url
    ) {
    }

    public record Checkout(
            String url
    ) {
    }
}
