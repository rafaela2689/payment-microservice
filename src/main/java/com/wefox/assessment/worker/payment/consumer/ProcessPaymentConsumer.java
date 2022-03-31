package com.wefox.assessment.worker.payment.consumer;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.payment.exceptions.InvalidPaymentTypeException;
import com.wefox.assessment.core.log.dto.ErrorType;
import com.wefox.assessment.core.log.LogService;
import com.wefox.assessment.core.payment.ProcessPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentConsumer {

    private final Logger LOG = LoggerFactory.getLogger(ProcessPaymentConsumer.class);

    private final ProcessPaymentService service;
    private final LogService logService;

    @Autowired
    public ProcessPaymentConsumer(final ProcessPaymentService paymentService,
                                  final LogService logService) {
        this.service = paymentService;
        this.logService = logService;
    }

    @KafkaListener(topics = "${process-payment.online.topic}",
            groupId = "${process-payment.online.group-id}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void consumePaymentOnline(final PaymentDTO paymentDTO) {
        LOG.info("Message received from topic online.");
        try {
            this.service.processPaymentOnline(paymentDTO);
        } catch (final Exception ex) {
            LOG.warn("Error while processing payment data! {}", ex.getMessage());
            this.logService.saveError(
                    paymentDTO.getPaymentId(),
                    ErrorType.other,
                    String.format("Something went wrong while processing payment %s. Error: %s",
                            paymentDTO.getPaymentId(), ex.getMessage())
            );
        }
    }

    @KafkaListener(topics = "${process-payment.offline.topic}",
            groupId = "${process-payment.offline.group-id}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void consumePaymentOffline(final PaymentDTO paymentDTO) {
        LOG.info("Message received from topic offline.");
        try {
            this.service.processPaymentOffline(paymentDTO);
        } catch (final Exception ex) {
            LOG.warn("Error while processing payment data! {}", ex.getMessage());
            this.logService.saveError(
                    paymentDTO.getPaymentId(),
                    ErrorType.other,
                    String.format("Something went wrong while processing payment %s. Error: %s",
                            paymentDTO.getPaymentId(), ex.getMessage())
            );
        }
    }
}
