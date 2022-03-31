package com.wefox.assessment.core.payment.exceptions;

public class InvalidPaymentPayloadException extends RuntimeException {

    public InvalidPaymentPayloadException() {}

    public String getMessage() {
        return "Invalid payment payload to process.";
    }
}
