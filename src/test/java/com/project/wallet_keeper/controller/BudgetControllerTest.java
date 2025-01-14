package com.project.wallet_keeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.entity.Budget;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetReport;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.util.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import com.project.wallet_keeper.service.BudgetService;
import com.project.wallet_keeper.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@WithMockUser
@DisplayName("BudgetController 테스트")
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private BudgetController budgetController;

    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Mock
    private User user;

    @Test
    @DisplayName("새로운 예산 저장 성공")
    void saveBudget() throws Exception {
        // given
        Budget budget = new Budget(10000, 2000, 1, user);
        BudgetDto budgetDto = new BudgetDto(10000, LocalDate.of(2000, 1, 1));
        BudgetResultDto budgetResultDto = new BudgetResultDto(budget, true);
        given(userService.getCurrentUser()).willReturn(user);
        given(budgetService.saveOrUpdate(any(User.class), any())).willReturn(budgetResultDto);

        // when
        ResultActions result = mockMvc.perform(put("/api/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("기존 예산 수정 성공")
    void updateBudget() throws Exception {
        // given
        Budget budget = new Budget(10000, 2000, 1, user);
        BudgetDto budgetDto = new BudgetDto(10000, LocalDate.of(2000, 1, 1));
        BudgetResultDto budgetResultDto = new BudgetResultDto(budget, false);
        given(userService.getCurrentUser()).willReturn(user);
        given(budgetService.saveOrUpdate(any(User.class), any())).willReturn(budgetResultDto);

        // when
        ResultActions result = mockMvc.perform(put("/api/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("예산 조회 성공")
    void getBudget() throws Exception {
        // given
        Budget budget = new Budget(10000, 2000, 1, user);
        given(userService.getCurrentUser()).willReturn(user);

        given(budgetService.findBudgetByDate(any(), anyInt(), anyInt())).willReturn(budget);

        // when
        ResultActions result = mockMvc.perform(get("/api/budget")
                .param("year", "1")
                .param("month", "1")
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("예산 보고서 조회 성공")
    void report() throws Exception {
        // given
        BudgetReport budgetReport = new BudgetReport(100000, 100000, 0, 100);

        given(userService.getCurrentUser()).willReturn(user);
        given(budgetService.report(any(), anyInt(), anyInt())).willReturn(budgetReport);

        // when
        ResultActions result = mockMvc.perform(get("/api/budget/report")
                .param("year", "1")
                .param("month", "1")
        );

        // then
        result.andExpect(status().isOk());
    }
}