package com.wefox.assessment.core.account;

import com.wefox.assessment.core.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
