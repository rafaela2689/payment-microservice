package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.account.Account;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;

    @NotNull(message = "Payment type is mandatory")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String creditCard;

    @NotNull(message = "Amount is mandatory")
    private Integer amount;

    @Column(updatable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    public static Payment from(final PaymentDTO paymentDTO) {
        final Payment payment = new Payment();
        payment.setPaymentId(paymentDTO.getPaymentId());
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setCreditCard(paymentDTO.getCreditCard());
        payment.setAmount(paymentDTO.getAmount());

        return payment;
    }
}
