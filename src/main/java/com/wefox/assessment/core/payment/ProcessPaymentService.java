package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.payment.exceptions.InvalidPaymentTypeException;
import com.wefox.assessment.core.paymentgateway.PaymentGatewayClient;
import com.wefox.assessment.core.paymentgateway.exceptions.UnableToValidatePayment;
import com.wefox.assessment.core.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProcessPaymentService {

    private final Logger LOG = LoggerFactory.getLogger(ProcessPaymentService.class);

    private final PaymentService paymentService;
    private final PaymentGatewayClient paymentGatewayClient;
    private final LogService logService;

    @Autowired
    public ProcessPaymentService(final PaymentService paymentService,
                          final PaymentGatewayClient paymentGatewayClient,
                          final LogService logClient) {
        this.paymentService = paymentService;
        this.paymentGatewayClient = paymentGatewayClient;
        this.logService = logClient;
    }

    @Transactional
    public void processPaymentOnline(final PaymentDTO paymentDTO) {
        if (!paymentDTO.isOnline()) {
            throw new InvalidPaymentTypeException(paymentDTO.getPaymentId());
        }
        try {
            LOG.info("Validating payment {} on payment gateway.", paymentDTO);
            this.paymentGatewayClient.validatePayment(paymentDTO);
            this.paymentService.processPayment(paymentDTO);
        }  catch (final UnableToValidatePayment ex) {
            LOG.warn("Invalid payment {}! Error: {}", paymentDTO.getPaymentId(), ex.getMessage());
            this.logService.saveError(
                    paymentDTO.getPaymentId(),
                    ErrorType.network,
                    String.format("Error while validating payment %s against payment gateway.", paymentDTO.getPaymentId())
            );
        }
    }

    @Transactional
    public void processPaymentOffline(final PaymentDTO paymentDTO) {
        if (!paymentDTO.isOffline()) {
            throw new InvalidPaymentTypeException(paymentDTO.getPaymentId());
        }
        this.paymentService.processPayment(paymentDTO);
    }

}
