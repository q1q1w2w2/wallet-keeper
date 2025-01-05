package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.Budget;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetReport;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.exception.budget.BudgetNotFoundException;
import com.project.wallet_keeper.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public BudgetResultDto saveOrUpdate(User user, BudgetDto budgetDto) {
        int year = budgetDto.getDate().getYear();
        int month = budgetDto.getDate().getMonthValue();

        Optional<Budget> findBudget = budgetRepository.findByUserAndYearAndMonth(user, year, month);

        if (findBudget.isEmpty()) {
            Budget budget = Budget.builder()
                    .user(user)
                    .amount(budgetDto.getAmount())
                    .year(budgetDto.getDate().getYear())
                    .month(budgetDto.getDate().getMonthValue())
                    .build();
            Budget saveBudget = budgetRepository.save(budget);
            return new BudgetResultDto(saveBudget, true);
        } else {
            Budget budget = findBudget.get();
            Budget updateBudget = budget.update(budgetDto.getAmount());
            return new BudgetResultDto(updateBudget, false);
        }
    }

    public Budget findBudgetByDate(User user, int year, int month) {
        return budgetRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseGet(() -> new Budget(0, year, month, user));
    }

    public BudgetReport report(User user, int year, int month) {
        int budget = findBudgetByDate(user, year, month).getAmount();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        List<TransactionResponseDto> expenseList = transactionService.getExpenseList(user, startDate, endDate);

        int totalAmount = 0;
        for (TransactionResponseDto dto : expenseList) {
            totalAmount += dto.getAmount();
        }
        int remainAmount = budget - totalAmount;
        int percent;
        if (budget > 0) {
            percent = (int) ((double) totalAmount / budget * 100);
        } else {
            percent = 0;
        }

        return new BudgetReport(budget, totalAmount, remainAmount, percent);
    }
}
