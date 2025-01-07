package com.project.wallet_keeper.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.IncomeCategory;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.security.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import com.project.wallet_keeper.service.TransactionService;
import com.project.wallet_keeper.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@WithMockUser
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionController transactionController;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private User user;

    @MockitoBean
    private IncomeCategory incomeCategory;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @DisplayName("수입 저장 성공")
    void saveIncome() throws Exception {
        // given
        TransactionDto incomeDto = new TransactionDto("내용", 10000, "", LocalDateTime.of(2000, 1, 1, 0, 0), 1L);
        Income income = Income.builder()
                .detail("내용")
                .amount(10000)
                .user(user)
                .incomeAt(LocalDateTime.of(2000, 1, 1, 0, 0))
                .incomeCategory(incomeCategory)
                .build();

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.saveIncome(any(User.class), any(TransactionDto.class))).willReturn(income);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/income")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeDto))
                .with(csrf()));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(10000));
    }

    @Test
    @DisplayName("지출 저장 성공")
    void saveExpense() {
    }

    @Test
    void getTransactionList() {
    }

    @Test
    void getExpenseList() {
    }

    @Test
    void getIncome() {
    }

    @Test
    void getExpense() {
    }

    @Test
    void updateIncome() {
    }

    @Test
    void updateExpense() {
    }

    @Test
    void deleteIncome() {
    }

    @Test
    void deleteExpense() {
    }

    @Test
    void getExpenseSummary() {
    }

    @Test
    void getAnnualSummary() {
    }
}