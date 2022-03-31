package com.wefox.assessment.core.log.exceptions;

import com.wefox.assessment.core.log.dto.ErrorDTO;

public class UnableToSaveErrorException extends RuntimeException {
    private final ErrorDTO errorDTO;
    private final Exception ex;

    public UnableToSaveErrorException(final ErrorDTO errorDTO, final Exception ex) {
        this.errorDTO = errorDTO;
        this.ex = ex;
    }

    public String getMessage() {
        return String.format("Unable to save error log %s. Error: %s", this.errorDTO.toString(), this.ex.getMessage());
    }
}
