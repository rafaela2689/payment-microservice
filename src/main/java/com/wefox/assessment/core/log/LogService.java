package com.wefox.assessment.core.log;

import com.wefox.assessment.core.log.dto.ErrorDTO;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.log.exceptions.InvalidErrorPayloadException;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogClient logClient;

    public LogService(final LogClient logClient) {
        this.logClient = logClient;
    }

    public void saveError(final String paymentId, final ErrorType errorType, final String errorDescription) {
        final ErrorDTO errorDTO = ErrorDTO.builder()
                .paymentId(paymentId)
                .error(errorType)
                .errorDescription(errorDescription)
                .build();
        if (!errorDTO.isValid()) {
            throw new InvalidErrorPayloadException();
        }
        this.logClient.saveError(errorDTO);
    }
}
