package com.wefox.assessment.core.log;

import com.wefox.assessment.core.log.dto.ErrorDTO;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.log.exceptions.UnableToSaveErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class LogClientTest {

    private LogClient logClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        final String baseUrl = "http://localhost:9000";
        this.logClient = new LogClient(baseUrl, restTemplate);
    }

    @Test
    void shouldThrowsWhenRequestFails() {
        final ErrorDTO errorDTO = ErrorDTO.builder()
                .error(ErrorType.network)
                .paymentId("abcd-1234")
                .errorDescription("error")
                .build();

        when(this.restTemplate.exchange(any(String.class), any(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(UnableToSaveErrorException.class, () -> {
            this.logClient.saveError(errorDTO);
        });
    }

    @Test
    void shouldBeProcessedCorrectlyWhenRequestIsSuccessful() {
        final ErrorDTO errorDTO = ErrorDTO.builder()
                .error(ErrorType.network)
                .paymentId("abcd-1234")
                .errorDescription("error")
                .build();

        when(this.restTemplate.exchange(any(String.class), any(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(errorDTO, HttpStatus.OK));

        this.logClient.saveError(errorDTO);

        verify(this.restTemplate, times(1))
                .exchange(eq("http://localhost:9000/log"), eq(HttpMethod.POST), any(), eq(String.class));
    }
}
