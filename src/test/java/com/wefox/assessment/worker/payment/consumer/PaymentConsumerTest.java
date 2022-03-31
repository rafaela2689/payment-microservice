package com.wefox.assessment.worker.payment.consumer;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.payment.PaymentType;
import com.wefox.assessment.core.payment.ProcessPaymentService;
import com.wefox.assessment.worker.payment.consumer.ProcessPaymentConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentConsumerTest {

    @InjectMocks
    private ProcessPaymentConsumer paymentConsumer;

    @Mock
    private ProcessPaymentService processPaymentService;

    @Test
    void shouldCallProcessPaymentOnline() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-1234")
                .accountId(1)
                .amount(100)
                .creditCard("1234567")
                .paymentType(PaymentType.online)
                .build();

        paymentConsumer.consumePaymentOnline(paymentDTO);

        verify(processPaymentService, times(1)).processPaymentOnline(paymentDTO);
        verify(processPaymentService, never()).processPaymentOffline(any());
    }

    @Test
    void shouldCallProcessPaymentOffline() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-1234")
                .accountId(1)
                .amount(100)
                .creditCard("1234567")
                .paymentType(PaymentType.offline)
                .build();

        paymentConsumer.consumePaymentOffline(paymentDTO);

        verify(processPaymentService, times(1)).processPaymentOffline(paymentDTO);
        verify(processPaymentService, never()).processPaymentOnline(any());
    }
}
