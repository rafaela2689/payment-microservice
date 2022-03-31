package com.wefox.assessment.core.account.exceptions;

public class AccountNotFoundException extends RuntimeException {

    private final Integer accountId;

    public AccountNotFoundException(final Integer accountId) {
        this.accountId = accountId;
    }

    public String getMessage() {
        return String.format("Account %d not found!", this.accountId);
    }
}
