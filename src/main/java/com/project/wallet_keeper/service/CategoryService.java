package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.ExpenseCategory;
import com.project.wallet_keeper.domain.IncomeCategory;
import com.project.wallet_keeper.dto.CreateCategoryDto;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    public IncomeCategory createIncomeCategory(CreateCategoryDto categoryDto) {
        IncomeCategory incomeCategory = new IncomeCategory(categoryDto.getCategoryName());
        return incomeCategoryRepository.save(incomeCategory);
    }

    @Transactional
    public ExpenseCategory createExpenseCategory(CreateCategoryDto categoryDto) {
        ExpenseCategory expenseCategory = new ExpenseCategory(categoryDto.getCategoryName());
        return expenseCategoryRepository.save(expenseCategory);
    }
}
