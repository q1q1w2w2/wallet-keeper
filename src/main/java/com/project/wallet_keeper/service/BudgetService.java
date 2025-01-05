package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.Budget;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.exception.budget.BudgetNotFoundException;
import com.project.wallet_keeper.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;

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
                .orElseThrow(BudgetNotFoundException::new);
    }
}
