package com.wefox.assessment.core.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wefox.assessment.core.payment.PaymentType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("account_id")
    private Integer accountId;

    @JsonProperty("payment_type")
    private PaymentType paymentType;

    @JsonProperty("credit_card")
    private String creditCard;

    private Integer amount;

    public boolean isOnline() {
        return PaymentType.online.equals(this.paymentType);
    }

    public boolean isOffline() {
        return PaymentType.offline.equals(this.paymentType);
    }

    public boolean isValid() {
        return this.paymentId != null && this.paymentId.length() > 0
                && this.accountId != null && this.amount != null;
    }
}
