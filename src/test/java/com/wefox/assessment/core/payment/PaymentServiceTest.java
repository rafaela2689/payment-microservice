package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.account.AccountService;
import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.account.exceptions.AccountNotFoundException;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.account.Account;
import com.wefox.assessment.core.payment.exceptions.InvalidPaymentPayloadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private LogService logService;

    @InjectMocks
    private PaymentService service;

    @Test
    void shouldCallSaveRepositoryWhenSaveValidPayment() {
        final Account account = Account.builder()
                .accountId(1)
                .birthdate(new Date())
                .email("email@email.com")
                .name("Mary")
                .createdOn(LocalDateTime.now())
                .build();
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        when(this.accountService.getById(1)).thenReturn(account);

        this.service.save(paymentDTO);

        verify(this.accountService, times(1)).getById(1);
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void shouldNotCallSaveWhenPaymentPayloadIsInvalid() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId(null)
                .accountId(null)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(null)
                .build();

        assertThrows(InvalidPaymentPayloadException.class,
                () -> this.service.save(paymentDTO));

        verifyNoInteractions(this.accountService, this.paymentRepository);
    }

    @Test
    void shouldSavePaymentWhenOnlineAndIsValid() {
        final Account account = Account.builder()
                .accountId(1)
                .birthdate(new Date())
                .email("email@email.com")
                .name("Mary")
                .createdOn(LocalDateTime.now())
                .build();
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.online)
                .creditCard("123456789")
                .amount(123)
                .build();

        when(this.accountService.getById(1)).thenReturn(account);

        final Payment payment = Payment.from(paymentDTO);
        when(this.paymentRepository.save(any())).thenReturn(payment);

        this.service.processPayment(paymentDTO);

        verify(paymentRepository, times(1)).save(any());
        verify(accountService, times(1)).update(any(), any());
        verifyNoInteractions(this.logService);
    }

    @Test
    void shouldCallSaveErrorWhenSavingAccountThrows() {
        final PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId("abcd-123")
                .accountId(1)
                .paymentType(PaymentType.offline)
                .creditCard("123456789")
                .amount(123)
                .build();


        final Payment payment = Payment.from(paymentDTO);
        when(this.paymentRepository.save(any())).thenReturn(payment);
        when(this.accountService.getById(1)).thenThrow(new AccountNotFoundException(1));

        this.service.processPayment(paymentDTO);

        verify(this.paymentRepository, never()).save(any());
        verify(this.accountService, never()).update(any(), any());
        verify(this.logService, times(1)).saveError(any(), any(), any());
    }
}
