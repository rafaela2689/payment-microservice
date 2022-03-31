package com.wefox.assessment.core.log;

import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.log.exceptions.InvalidErrorPayloadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Mock
    private LogClient logClient;

    @Test
    void shouldCallLogClient() {
        this.logService.saveError("abcd-1234", ErrorType.database, "Something went wrong!");

        verify(this.logClient, times(1)).saveError(any());
    }

    @Test
    void shouldThrowsIfCallPayloadIsInvalid() {
        assertThrows(InvalidErrorPayloadException.class,
                () -> this.logService.saveError(null, ErrorType.database, "Something went wrong!"));

        verify(this.logClient, never()).saveError(any());
    }
}
