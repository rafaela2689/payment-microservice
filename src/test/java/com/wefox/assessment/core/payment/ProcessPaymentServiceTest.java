package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.payment.exceptions.InvalidPaymentTypeException;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.paymentgateway.PaymentGatewayClient;
import com.wefox.assessment.core.paymentgateway.exceptions.UnableToValidatePayment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ProcessPaymentServiceTest {

    @Mock
    private PaymentGatewayClient paymentProviderClient;

    @Mock
    private LogService logService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ProcessPaymentService service;

    @Test
    void shouldNotCallProcessPaymentWhenPaymentIsNotValid() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        when(this.paymentProviderClient.validatePayment(paymentDTO))
                .thenThrow(new UnableToValidatePayment(paymentDTO, new Exception("error")));

        this.service.processPaymentOnline(paymentDTO);

        verifyNoInteractions(paymentService);
        verify(this.logService, times(1))
                .saveError(eq(paymentDTO.getPaymentId()), eq(ErrorType.network), any());
    }

    @Test
    void shouldSavePaymentWhenOnlineAndIsValid() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        this.service.processPaymentOnline(paymentDTO);

        verify(this.paymentProviderClient, times(1)).validatePayment(paymentDTO);
        verify(this.paymentService, times(1)).processPayment(paymentDTO);
        verifyNoInteractions(this.logService);
    }

    @Test
    void shouldNotCallPaymentValidationWhenTypeIsOffline() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.offline)
                .creditCard("123456789")
                .amount(123)
                .build();

        this.service.processPaymentOffline(paymentDTO);

        verifyNoInteractions(this.paymentProviderClient);
        verify(paymentService, times(1)).processPayment(paymentDTO);
        verifyNoInteractions(this.logService);
    }

    @Test
    void shouldThrowsWhenTryToProcessPaymentOfflineWithTypeOnline() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        assertThrows(InvalidPaymentTypeException.class,
                () -> this.service.processPaymentOffline(paymentDTO));

        verifyNoInteractions(this.paymentService);
        verifyNoInteractions(this.logService);
    }

    @Test
    void shouldThrowsWhenTryToProcessPaymentOnlineWithTypeOffline() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.offline)
                .creditCard("123456789")
                .amount(123)
                .build();

        assertThrows(InvalidPaymentTypeException.class,
                () -> this.service.processPaymentOnline(paymentDTO));

        verifyNoInteractions(this.paymentProviderClient, this.paymentService, this.logService);
    }
}
