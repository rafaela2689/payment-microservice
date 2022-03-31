package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.account.exceptions.AccountNotFoundException;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.account.Account;
import com.wefox.assessment.core.account.AccountService;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.payment.exceptions.InvalidPaymentPayloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PaymentService {

    private final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final LogService logService;

    @Autowired
    public PaymentService(final PaymentRepository paymentRepository,
                          final AccountService accountService,
                          final LogService logClient) {
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.logService = logClient;
    }

    @Transactional
    public Payment save(final PaymentDTO paymentDTO) {
        if (!paymentDTO.isValid()) {
            throw new InvalidPaymentPayloadException();
        }
        final Account account =  this.accountService.getById(paymentDTO.getAccountId());
        final Payment payment = Payment.from(paymentDTO);
        payment.setAccount(account);
        return this.paymentRepository.save(payment);
    }

    @Transactional
    public void processPayment(final PaymentDTO paymentDTO) {
        try {
            final Payment savedPayment = this.save(paymentDTO);
            this.accountService.update(savedPayment.getAccount(), savedPayment.getCreatedOn());
            LOG.info("Payment {} created on database!", savedPayment);
        } catch (final AccountNotFoundException ex) {
            LOG.warn("Account {} not found! Error: {}", paymentDTO.getAccountId(), ex.getMessage());
            this.logService.saveError(paymentDTO.getPaymentId(),
                    ErrorType.database,
                    String.format("Error while saving payment %s. Account %d not found!", paymentDTO.getPaymentId(), paymentDTO.getAccountId()));
        }
    }
}
