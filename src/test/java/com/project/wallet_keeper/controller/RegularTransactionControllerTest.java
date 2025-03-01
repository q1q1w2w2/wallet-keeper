package com.project.wallet_keeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.entity.*;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.util.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import com.project.wallet_keeper.service.RegularTransactionService;
import com.project.wallet_keeper.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegularTransactionController.class)
@WithMockUser
class RegularTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RegularTransactionController regularTransactionController;

    @MockitoBean
    private RegularTransactionService regularTransactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private User user;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static String detail = "내용";
    private static int amount = 100000;
    private static LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static Long categoryId = 1L;

    @Test
    @DisplayName("정기 수입 저장 성공")
    void saveRegularIncome() throws Exception {
        // given
        RegularIncome regularIncome = createRegularIncome();
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(userService.getCurrentUser()).willReturn(user);
        given(regularTransactionService.saveRegularIncome(any(), any())).willReturn(regularIncome);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/scheduler/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("정기 지출 저장 성공")
    void saveRegularExpense() throws Exception {
        // given
        RegularExpense regularExpense = createRegularExpense();
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(userService.getCurrentUser()).willReturn(user);
        given(regularTransactionService.saveRegularExpense(any(), any())).willReturn(regularExpense);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/scheduler/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("정기 거래 목록 조회 성공")
    void getRegularTransactions() throws Exception {
        // given
        ArrayList<RegularTransactionResponseDto> list = new ArrayList<>();

        given(userService.getCurrentUser()).willReturn(user);
        given(regularTransactionService.getRegularTransactions(any())).willReturn(list);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/scheduler"));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("정기 수입 항목 수정 성공")
    void updateRegularIncome() throws Exception {
        // given
        RegularIncome regularIncome = createRegularIncome();
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(userService.getCurrentUser()).willReturn(user);
        given(regularTransactionService.updateRegularIncome(any(), any(), any())).willReturn(regularIncome);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/scheduler/income/{incomeId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("정기 지출 항목 수정 성공")
    void updateRegularExpense() throws Exception {
        // given
        RegularExpense regularExpense = createRegularExpense();
        TransactionDto transactionDto = new TransactionDto(detail, amount, null, date, categoryId);

        given(userService.getCurrentUser()).willReturn(user);
        given(regularTransactionService.updateRegularExpense(any(), any(), any())).willReturn(regularExpense);

        // when
        ResultActions result = mockMvc.perform(patch("/api/transaction/scheduler/expense/{expenseId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("정기 수입 항목 삭제 성공")
    void deleteRegularIncome() throws Exception {
        // given
        given(userService.getCurrentUser()).willReturn(user);
        doNothing().when(regularTransactionService).deleteRegularIncome(any(), any());

        // when
        ResultActions result = mockMvc.perform(delete("/api/transaction/scheduler/income/{incomeId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("정기 지출 항목 삭제 성공")
    void deleteRegularExpense() throws Exception {
        // given
        given(userService.getCurrentUser()).willReturn(user);
        doNothing().when(regularTransactionService).deleteRegularExpense(any(), any());

        // when
        ResultActions result = mockMvc.perform(delete("/api/transaction/scheduler/expense/{expenseId}", 1L)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent());
    }

    private RegularIncome createRegularIncome() {
        return RegularIncome.builder()
                .user(user)
                .detail(detail)
                .amount(amount)
                .incomeCategory(createIncomeCategory())
                .incomeAt(date)
                .build();
    }

    private RegularExpense createRegularExpense() {
        return RegularExpense.builder()
                .user(user)
                .detail(detail)
                .amount(amount)
                .expenseCategory(createExpenseCategory())
                .expenseAt(date)
                .build();
    }

    private ExpenseCategory createExpenseCategory() {
        return new ExpenseCategory("카테고리");
    }

    private IncomeCategory createIncomeCategory() {
        return new IncomeCategory("카테고리");
    }
}