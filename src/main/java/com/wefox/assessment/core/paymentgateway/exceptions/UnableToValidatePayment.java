package com.wefox.assessment.core.paymentgateway.exceptions;

import com.wefox.assessment.core.payment.dto.PaymentDTO;

public class UnableToValidatePayment extends RuntimeException {

    private final PaymentDTO paymentDTO;
    private final Exception ex;

    public UnableToValidatePayment(final PaymentDTO paymentDTO, final Exception ex) {
        this.paymentDTO = paymentDTO;
        this.ex = ex;
    }

    public String getMessage() {
        return String.format("Unable to validate payment %s. Error: %s", this.paymentDTO.toString(), this.ex.getMessage());
    }
}

