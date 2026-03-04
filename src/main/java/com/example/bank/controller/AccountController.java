package com.example.bank.controller;

import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAll() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account getById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @PutMapping("/{id}")
    public Account update(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.getOrDefault("amount", BigDecimal.ZERO);
        return accountService.deposit(id, amount);
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.getOrDefault("amount", BigDecimal.ZERO);
        return accountService.withdraw(id, amount);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

