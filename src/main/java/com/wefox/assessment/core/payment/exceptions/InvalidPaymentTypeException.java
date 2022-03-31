package com.wefox.assessment.core.payment.exceptions;

public class InvalidPaymentTypeException extends RuntimeException {

    private final String paymentId;

    public InvalidPaymentTypeException(final String paymentId) {
        this.paymentId = paymentId;
    }

    public String getMessage() {
        return String.format("Payment %s cannot be processed.", this.paymentId);
    }
}
