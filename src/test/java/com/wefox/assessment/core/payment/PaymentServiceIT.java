package com.wefox.assessment.core.payment;

import com.wefox.assessment.AssessmentApplicationTests;
import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.account.exceptions.AccountNotFoundException;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.log.dto.ErrorType;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AssessmentApplicationTests.class)
public class PaymentServiceIT {

    @MockBean
    private LogService logService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    @InjectMocks
    private PaymentService service;

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldSavePaymentWhenAccountExists() {

        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        final Payment payment = this.service.save(paymentDTO);

        assertEquals(payment.getPaymentId(), paymentDTO.getPaymentId());
        assertEquals(payment.getPaymentType(), paymentDTO.getPaymentType());
        assertEquals(payment.getCreditCard(), paymentDTO.getCreditCard());
        assertEquals(payment.getAmount(), paymentDTO.getAmount());
        assertEquals(payment.getAccount().getAccountId(), paymentDTO.getAccountId());
    }

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldNotSavePaymentWhenAccountDoesNotExist() {

        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(2)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        assertThrows(AccountNotFoundException.class, () -> {
            this.service.save(paymentDTO);
        });

        final Optional<Payment> payment = this.paymentRepository.findById(paymentDTO.getPaymentId());
        assertTrue(payment.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldSavePaymentAndAccountWhenProcessPayment() {

        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        this.service.processPayment(paymentDTO);

        final Payment payment = this.paymentRepository.findById(paymentDTO.getPaymentId()).orElseThrow();

        assertEquals(payment.getPaymentId(), paymentDTO.getPaymentId());
        assertEquals(payment.getPaymentType(), paymentDTO.getPaymentType());
        assertEquals(payment.getCreditCard(), paymentDTO.getCreditCard());
        assertEquals(payment.getAmount(), paymentDTO.getAmount());
        assertEquals(payment.getAccount().getAccountId(), paymentDTO.getAccountId());
        assertEquals(payment.getAccount().getLastPaymentDate(), payment.getCreatedOn());
    }

    @Test
    @Transactional
    @Rollback
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/beforeTestSeedAccount.sql")
    void shouldCallLogServiceWhenAccountDoesNotExistAndNotSavePayment() {

        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(2)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();


        this.service.processPayment(paymentDTO);

        final Optional<Payment> payment = this.paymentRepository.findById(paymentDTO.getPaymentId());

        assertTrue(payment.isEmpty());
        verify(this.logService, times(1))
                .saveError(eq(paymentDTO.getPaymentId()), eq(ErrorType.database), anyString());
    }

}
