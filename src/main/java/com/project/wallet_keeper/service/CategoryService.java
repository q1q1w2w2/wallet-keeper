package com.project.wallet_keeper.service;

import com.project.wallet_keeper.dto.category.CategoryResponseDto;
import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.exception.transaction.CategoryAlreadyExistException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    @CacheEvict(value = "incomeCategories", allEntries = true)
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
    @CacheEvict(value = "expenseCategories", allEntries = true)
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

    @Cacheable(value = "incomeCategories")
    public List<CategoryResponseDto> getIncomeCategories() {
        ArrayList<CategoryResponseDto> response = new ArrayList<>();

        List<IncomeCategory> categoryList = incomeCategoryRepository.findAllByIsDeletedFalse();
        for (IncomeCategory incomeCategory : categoryList) {
            response.add(new CategoryResponseDto(incomeCategory));
        }

        return response;
    }

    @Cacheable(value = "expenseCategories")
    public List<CategoryResponseDto> getExpenseCategories() {
        ArrayList<CategoryResponseDto> response = new ArrayList<>();

        List<ExpenseCategory> categoryList = expenseCategoryRepository.findAllByIsDeletedFalse();
        for (ExpenseCategory expenseCategory : categoryList) {
            response.add(new CategoryResponseDto(expenseCategory));
        }

        return response;
    }

    @Transactional
    @CacheEvict(value = "incomeCategories", allEntries = true)
    public void deleteIncomeCategory(Long categoryId) {
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);
        category.delete();
    }

    @Transactional
    @CacheEvict(value = "expenseCategories", allEntries = true)
    public void deleteExpenseCategory(Long categoryId) {
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);
        category.delete();
    }
}
