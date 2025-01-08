package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.Budget;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.repository.BudgetRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    private static int amount = 100000;
    private static int year = 2025;
    private static int month = 1;

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
}