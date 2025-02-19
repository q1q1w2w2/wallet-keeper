package com.project.wallet_keeper.service;

import com.project.wallet_keeper.dto.budget.BudgetReport;
import com.project.wallet_keeper.entity.Budget;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.repository.BudgetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BudgetService 테스트")
class BudgetServiceTest {

    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private User user;

    private static final int amount = 100000;
    private static final int year = 2025;
    private static final int month = 1;

    @Test
    @DisplayName("초기 예산 저장 성공")
    void saveOrUpdate() {
        // given
        BudgetDto budgetDto = new BudgetDto(amount, LocalDate.of(year, month, 1));
        Budget budget = new Budget(amount, year, month, user);

        given(budgetRepository.findByUserAndYearAndMonth(any(), anyInt(), anyInt())).willReturn(Optional.empty());
        given(budgetRepository.save(any())).willReturn(budget);

        // when
        BudgetResultDto budgetResult = budgetService.saveOrUpdate(user, budgetDto);

        // then
        assertThat(budgetResult.getIsNew()).isTrue();
        assertThat(budgetResult.getBudget().getAmount()).isEqualTo(amount);
        assertThat(budgetResult.getBudget().getYear()).isEqualTo(year);
        assertThat(budgetResult.getBudget().getMonth()).isEqualTo(month);
    }

    @Test
    @DisplayName("기존 예산 수정 성공")
    void saveOrUpdate2() {
        // given
        int newAmount = 200000;
        BudgetDto budgetDto = new BudgetDto(newAmount, LocalDate.of(year, month, 1));
        Budget budget = spy(new Budget(amount, year, month, user));

        given(budgetRepository.findByUserAndYearAndMonth(any(), anyInt(), anyInt())).willReturn(Optional.of(budget));

        // when
        BudgetResultDto budgetResult = budgetService.saveOrUpdate(user, budgetDto);

        // then
        assertThat(budgetResult.getIsNew()).isFalse();
        assertThat(budgetResult.getBudget().getAmount()).isEqualTo(newAmount);
        assertThat(budgetResult.getBudget().getYear()).isEqualTo(year);
        assertThat(budgetResult.getBudget().getMonth()).isEqualTo(month);

        verify(budget).update(budgetDto.getAmount());
    }

    @Test
    @DisplayName("날짜에 맞는 예산 조회 성공")
    void findBudgetByDate() {
        // given
        Budget budget = new Budget(amount, year, month, user);
        given(budgetRepository.findByUserAndYearAndMonth(any(), anyInt(), anyInt())).willReturn(Optional.of(budget));

        // when
        Budget findBudget = budgetService.findBudgetByDate(user, year, month);

        // then
        assertThat(findBudget).isNotNull();
        assertThat(findBudget.getAmount()).isEqualTo(amount);
        assertThat(findBudget.getYear()).isEqualTo(year);
        assertThat(findBudget.getMonth()).isEqualTo(month);
    }

    @Test
    @DisplayName("예산 요약 반환 성공")
    void report() {
        // given
        Budget budget = new Budget(100000, 2025, 2, user);
        given(budgetRepository.findByUserAndYearAndMonth(user, 2025, 2))
                .willReturn(Optional.of(budget));
        given(transactionService.getExpenseList(any(), any(), any()))
                .willReturn(List.of());

        // when
        BudgetReport report = budgetService.report(user, 2025, 2);

        // then
        assertThat(report.getBudget()).isEqualTo(100000);
    }
}