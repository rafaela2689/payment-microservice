package com.wefox.assessment.core.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService service;

    @Test
    void shouldCallSaveWhenAccountExists() {
        final Account account = Account.builder()
                .accountId(1)
                .birthdate(new Date())
                .email("email@email.com")
                .name("Mary")
                .createdOn(LocalDateTime.now())
                .build();

        when(this.accountRepository.findById(1)).thenReturn(Optional.of(account));
        final LocalDateTime lastPaymentDate = LocalDateTime.of(2022, 3, 28, 0, 0, 0);

        this.service.update(account, lastPaymentDate);

        verify(this.accountRepository, times(1)).save(any());
        assertEquals(account.getLastPaymentDate(), lastPaymentDate);
    }

}
