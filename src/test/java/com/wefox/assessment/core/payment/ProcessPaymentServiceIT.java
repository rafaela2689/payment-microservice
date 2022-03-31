package com.wefox.assessment.core.payment;

import com.wefox.assessment.AssessmentApplicationTests;
import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.paymentgateway.PaymentGatewayClient;
import com.wefox.assessment.core.paymentgateway.exceptions.UnableToValidatePayment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AssessmentApplicationTests.class)
public class ProcessPaymentServiceIT {

    @MockBean
    private PaymentGatewayClient paymentGatewayClient;

    @MockBean
    private LogService logService;

    @Autowired
    @InjectMocks
    private ProcessPaymentService service;

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldNotSavePaymentWhenPaymentIsInvalid() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        when(this.paymentGatewayClient.validatePayment(paymentDTO))
                .thenThrow(new UnableToValidatePayment(paymentDTO, new Exception("error")));

        this.service.processPaymentOnline(paymentDTO);

        verify(this.logService, times(1))
                .saveError(eq(paymentDTO.getPaymentId()), eq(ErrorType.network), anyString());
    }

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldProcessPaymentOnlineWhenIsValid() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        this.service.processPaymentOnline(paymentDTO);

        verify(this.paymentGatewayClient, times(1)).validatePayment(paymentDTO);
        verify(this.logService, never()).saveError(any(), any(), anyString());
    }
}
