package com.wefox.assessment.core.log.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    @JsonProperty("payment_id")
    private String paymentId;

    private ErrorType error;

    @JsonProperty("error_description")
    private String errorDescription;

    public boolean isValid() {
        return this.paymentId != null && this.paymentId.length() > 0
                && this.error != null;
    }
}
