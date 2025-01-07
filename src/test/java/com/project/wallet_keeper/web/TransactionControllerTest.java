package com.project.wallet_keeper.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ExpenseCategory expenseCategory;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static String DETAIL = "내용";
    private static Integer AMOUNT = 10000;
    private static LocalDateTime AT = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static String INCOME = "INCOME";
    private static String EXPENSE = "EXPENSE";

    @Test
    @DisplayName("수입 저장 성공")
    void saveIncome() throws Exception {
        // given
        TransactionDto incomeDto = new TransactionDto(DETAIL, AMOUNT, null, AT, 1L);
        Income income = createIncome();

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.saveIncome(any(User.class), any(TransactionDto.class))).willReturn(income);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/income")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT));
    }

    @Test
    @DisplayName("지출 저장 성공")
    void saveExpense() throws Exception {
        // given
        TransactionDto expenseDto = new TransactionDto(DETAIL, AMOUNT, null, AT, 1L);
        Expense expense = createExpense();

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.saveExpense(any(User.class), any(TransactionDto.class))).willReturn(expense);

        // when
        ResultActions result = mockMvc.perform(post("/api/transaction/expense")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT));
    }

    @Test
    @DisplayName("거래 내역 조회 성공")
    void getTransactionList() throws Exception {
        // given
        List<TransactionResponseDto> transactionList = new ArrayList<>();
        TransactionResponseDto dto =
                new TransactionResponseDto(1L, DETAIL, AMOUNT, null, "카테고리", AT, INCOME);
        transactionList.add(dto);

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.getTransactionList(any(User.class), any(LocalDate.class), any(LocalDate.class))).willReturn(transactionList);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/list")
                .param("startDate", "2000-01-01")
                .param("endDate", "2000-12-31")
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].detail").value(DETAIL))
                .andExpect(jsonPath("$.data[0].amount").value(AMOUNT))
                .andExpect(jsonPath("$.data[0].transactionType").value(INCOME));
    }

    @Test
    @DisplayName("지출 내역 조회 성공")
    void getExpenseList() throws Exception {
        // given
        List<TransactionResponseDto> expenseList = new ArrayList<>();
        TransactionResponseDto dto =
                new TransactionResponseDto(1L, DETAIL, AMOUNT, null, "카테고리", AT, EXPENSE);
        expenseList.add(dto);

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.getExpenseList(any(User.class), any(LocalDate.class), any(LocalDate.class))).willReturn(expenseList);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/expense/list")
                .param("startDate", "2000-01-01")
                .param("endDate", "2000-12-31")
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].detail").value(DETAIL))
                .andExpect(jsonPath("$.data[0].amount").value(AMOUNT))
                .andExpect(jsonPath("$.data[0].transactionType").value(EXPENSE));
    }

    @Test
    @DisplayName("수입 항목 조회 성공")
    void getIncome() throws Exception {
        // given
        Income income = createIncome();
        given(transactionService.getIncome(anyLong())).willReturn(income);

        // when
        ResultActions result =
                mockMvc.perform(get("/api/transaction/income/{incomeId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT));
    }

    @Test
    @DisplayName("지출 항목 조회 성공")
    void getExpense() throws Exception {
        // given
        Expense expense = createExpense();
        given(transactionService.getExpense(anyLong())).willReturn(expense);

        // when
        ResultActions result =
                mockMvc.perform(get("/api/transaction/expense/{expenseId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT));
    }

    @Test
    @DisplayName("수입 항목 수정 성공")
    void updateIncome() throws Exception {
        // given
        Income income = createIncome();
        TransactionDto transactionDto =
                new TransactionDto(DETAIL, AMOUNT, null, AT, 1L);

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.updateIncome(anyLong(), any(TransactionDto.class), any(User.class))).willReturn(income);

        // when
        ResultActions result =
                mockMvc.perform(patch("/api/transaction/income/{incomeId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                        .with(csrf())
                );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT))
                .andExpect(jsonPath("$.data.transactionType").value(INCOME));
    }

    @Test
    @DisplayName("지출 항목 수정 성공")
    void updateExpense() throws Exception {
        // given
        Expense expense = createExpense();
        TransactionDto transactionDto =
                new TransactionDto(DETAIL, AMOUNT, null, AT, 1L);

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.updateExpense(anyLong(), any(TransactionDto.class), any(User.class))).willReturn(expense);

        // when
        ResultActions result =
                mockMvc.perform(patch("/api/transaction/expense/{expenseId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                        .with(csrf())
                );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.detail").value(DETAIL))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT))
                .andExpect(jsonPath("$.data.transactionType").value(EXPENSE));
    }

    @Test
    @DisplayName("수입 항목 삭제 성공")
    void deleteIncome() throws Exception {
        // given
        given(userService.getCurrentUser()).willReturn(user);

        // when
        ResultActions result =
                mockMvc.perform(delete("/api/transaction/income/{incomeId}", 1L)
                        .with(csrf())
                );

        // then
        result.andExpect(status().isNoContent());

        verify(transactionService).deleteIncome(eq(1L), eq(user));
    }

    @Test
    @DisplayName("지출 항목 삭제 성공")
    void deleteExpense() throws Exception {
        // given
        given(userService.getCurrentUser()).willReturn(user);

        // when
        ResultActions result =
                mockMvc.perform(delete("/api/transaction/expense/{expenseId}", 1L)
                        .with(csrf())
                );

        // then
        result.andExpect(status().isNoContent());

        verify(transactionService).deleteExpense(eq(1L), eq(user));
    }

    @Test
    @DisplayName("지출 요약 조회 성공")
    void getExpenseSummary() throws Exception {
        // given
        List<ExpenseSummary> summaryList = new ArrayList<>();
        summaryList.add(new ExpenseSummary(1L, "카테고리", AMOUNT, 100));

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.getExpenseSummary(any(User.class), any(LocalDate.class), any(LocalDate.class))).willReturn(summaryList);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/expense/summary")
                .param("startDate", "2000-01-01")
                .param("endDate", "2000-12-31")
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("연간 보고서 조회 성공")
    void getAnnualSummary() throws Exception {
        // given
        MonthlySummary monthlySummary = new MonthlySummary(10000, 100000, 0);
        HashMap<String, MonthlySummary> monthlySummaryHashMap = new HashMap<>();
        monthlySummaryHashMap.put("January", monthlySummary);
        AnnualSummary annualSummary = new AnnualSummary(2000, 1000000, 1000000, 0, monthlySummaryHashMap);

        given(userService.getCurrentUser()).willReturn(user);
        given(transactionService.getAnnualSummary(any(User.class), anyInt())).willReturn(annualSummary);

        // when
        ResultActions result = mockMvc.perform(get("/api/transaction/annual")
                .param("year", "2000")
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.year").value(2000));
    }

    private Income createIncome() {
        return Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(AT)
                .incomeCategory(incomeCategory)
                .build();
    }

    private Expense createExpense() {
        return Expense.builder()
                .detail(DETAIL)
                .user(user)
                .amount(AMOUNT)
                .expenseAt(AT)
                .expenseCategory(expenseCategory)
                .build();
    }
}