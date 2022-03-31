package com.wefox.assessment.core.payment;

import com.wefox.assessment.core.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
