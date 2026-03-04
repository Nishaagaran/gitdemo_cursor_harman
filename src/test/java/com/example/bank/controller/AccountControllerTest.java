package com.example.bank.controller;

import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsEmptyList() throws Exception {
        Mockito.when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void create_returnsCreatedAccount() throws Exception {
        Account request = new Account("ACC-1", "John Doe", new BigDecimal("100.00"));
        Account response = new Account("ACC-1", "John Doe", new BigDecimal("100.00"));
        response.setId(1L);

        Mockito.when(accountService.createAccount(any(Account.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountNumber", is("ACC-1")))
                .andExpect(jsonPath("$.ownerName", is("John Doe")));
    }

    @Test
    void deposit_updatesBalance() throws Exception {
        Account response = new Account("ACC-1", "John Doe", new BigDecimal("150.00"));
        response.setId(1L);

        Mockito.when(accountService.deposit(eq(1L), eq(new BigDecimal("50.00"))))
                .thenReturn(response);

        String body = """
                {"amount": 50.00}
                """;

        mockMvc.perform(post("/api/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(150.00)));
    }
}

