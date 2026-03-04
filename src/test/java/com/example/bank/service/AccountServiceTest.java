package com.example.bank.service;

import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createAccount_initializesBalanceIfNull() {
        Account account = new Account();
        account.setAccountNumber("ACC-1");
        account.setOwnerName("John Doe");
        account.setBalance(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account saved = accountService.createAccount(account);

        verify(accountRepository).save(captor.capture());
        assertEquals(BigDecimal.ZERO, captor.getValue().getBalance());
        assertEquals("ACC-1", saved.getAccountNumber());
    }

    @Test
    void deposit_increasesBalance() {
        Account account = new Account("ACC-1", "John Doe", new BigDecimal("100.00"));
        account.setId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.deposit(1L, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    void withdraw_decreasesBalance() {
        Account account = new Account("ACC-1", "John Doe", new BigDecimal("100.00"));
        account.setId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.withdraw(1L, new BigDecimal("40.00"));

        assertEquals(new BigDecimal("60.00"), account.getBalance());
    }

    @Test
    void withdraw_throwsWhenInsufficientBalance() {
        Account account = new Account("ACC-1", "John Doe", new BigDecimal("30.00"));
        account.setId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class,
                () -> accountService.withdraw(1L, new BigDecimal("50.00")));
    }
}

