package com.example.bank.service;

import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public Account createAccount(Account account) {
        account.setId(null);
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account updated) {
        Account existing = getAccountById(id);
        existing.setOwnerName(updated.getOwnerName());
        existing.setAccountNumber(updated.getAccountNumber());
        existing.setBalance(updated.getBalance());
        return accountRepository.save(existing);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = getAccountById(id);
        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = getAccountById(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return account;
    }
}

