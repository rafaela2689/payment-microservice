package com.wefox.assessment.core.account;

import com.wefox.assessment.core.account.exceptions.AccountNotFoundException;
import com.wefox.assessment.core.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class AccountService {

    private final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void update(final Account account, final LocalDateTime lastPaymentDate) {
        LOG.info("Updating account {} with last payment date {}", account.getAccountId(), lastPaymentDate);
        account.setLastPaymentDate(lastPaymentDate);
        accountRepository.save(account);
    }

    public Account getById(final Integer accountId) {
        return this.accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
