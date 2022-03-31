package com.wefox.assessment.core.log.exceptions;

public class InvalidErrorPayloadException extends RuntimeException {

    public InvalidErrorPayloadException() {}

    public String getMessage() {
        return "Invalid error payload.";
    }
}
