package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.exception.transaction.CategoryAlreadyExistException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    public IncomeCategory createIncomeCategory(CreateCategoryDto categoryDto) {
        Optional<IncomeCategory> findCategory = incomeCategoryRepository.findByCategoryName(categoryDto.getCategoryName());
        if (findCategory.isPresent()) {
            if (findCategory.get().isDeleted()) {
                findCategory.get().active();
                return findCategory.get();
            }
            throw new CategoryAlreadyExistException();
        }

        IncomeCategory incomeCategory = new IncomeCategory(categoryDto.getCategoryName());
        return incomeCategoryRepository.save(incomeCategory);
    }

    @Transactional
    public ExpenseCategory createExpenseCategory(CreateCategoryDto categoryDto) {
        Optional<ExpenseCategory> findCategory = expenseCategoryRepository.findByCategoryName(categoryDto.getCategoryName());
        if (findCategory.isPresent()) {
            if (findCategory.get().isDeleted()) {
                findCategory.get().active();
                return findCategory.get();
            }
            throw new CategoryAlreadyExistException();
        }

        ExpenseCategory expenseCategory = new ExpenseCategory(categoryDto.getCategoryName());
        return expenseCategoryRepository.save(expenseCategory);
    }

    public List<IncomeCategory> getIncomeCategories() {
        return incomeCategoryRepository.findAllByIsDeletedFalse();
    }

    public List<ExpenseCategory> getExpenseCategories() {
        return expenseCategoryRepository.findAllByIsDeletedFalse();
    }

    @Transactional
    public void deleteIncomeCategory(Long categoryId) {
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);
        category.delete();
    }

    @Transactional
    public void deleteExpenseCategory(Long categoryId) {
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);
        category.delete();
    }
}
