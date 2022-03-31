package com.wefox.assessment.core.paymentgateway;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.payment.PaymentType;
import com.wefox.assessment.core.paymentgateway.exceptions.UnableToValidatePayment;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentGatewayClientTest {

    private PaymentGatewayClient paymentProviderClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        final String baseUrl = "http://localhost:9000";
        this.paymentProviderClient = new PaymentGatewayClient(baseUrl, restTemplate);
    }

    @Test
    void shouldThrowsWhenRequestFails() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-1234")
                .accountId(1)
                .amount(100)
                .creditCard("1234567")
                .paymentType(PaymentType.online)
                .build();

        when(this.restTemplate.exchange(any(String.class), any(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(UnableToValidatePayment.class, () -> {
            this.paymentProviderClient.validatePayment(paymentDTO);
        });
    }

    @Test
    void shouldBeProcessedCorrectlyWhenRequestIsSuccessful() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-1234")
                .accountId(1)
                .amount(100)
                .creditCard("1234567")
                .paymentType(PaymentType.online)
                .build();

        when(this.restTemplate.exchange(any(String.class), any(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(paymentDTO, HttpStatus.OK));

        this.paymentProviderClient.validatePayment(paymentDTO);

        verify(this.restTemplate, times(1))
                .exchange(eq("http://localhost:9000/payment"), eq(HttpMethod.POST), any(), eq(String.class));
    }

}
