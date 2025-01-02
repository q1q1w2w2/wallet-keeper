package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.SaveIncomeDto;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    public Expense save(User user, SaveIncomeDto incomeDto) {
        Long categoryId = incomeDto.getIncomeCategoryId();
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        Expense expense = Expense.builder()
                .user(user)
                .detail(incomeDto.getDetail())
                .amount(incomeDto.getAmount())
                .description(incomeDto.getDescription() != null ? incomeDto.getDescription() : "")
                .expenseCategory(category)
                .expenseAt(incomeDto.getIncomeAt())
                .build();
        return expenseRepository.save(expense);
    }
}
