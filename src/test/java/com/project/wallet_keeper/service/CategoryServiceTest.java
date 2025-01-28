package com.project.wallet_keeper.service;

import com.project.wallet_keeper.dto.category.CategoryResponseDto;
import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.exception.transaction.CategoryAlreadyExistException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 테스트")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    private static final String categoryName = "카테고리";

    @Test
    @DisplayName("수입 카테고리 생성 성공")
    void createIncomeCategory() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);

        given(incomeCategoryRepository.findByCategoryName(any())).willReturn(Optional.empty());
        given(incomeCategoryRepository.save(any())).willReturn(new IncomeCategory(categoryName));

        // when
        IncomeCategory saveCategory = categoryService.createIncomeCategory(categoryDto);

        // then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    @DisplayName("수입 카테고리 생성 성공: 삭제된 카테고리 복구")
    void createIncomeCategory2() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        IncomeCategory category = new IncomeCategory(categoryName);
        category.delete();

        given(incomeCategoryRepository.findByCategoryName(any())).willReturn(Optional.of(category));

        // when
        IncomeCategory activeCategory = categoryService.createIncomeCategory(categoryDto);

        // then
        assertThat(activeCategory.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("수입 카테고리 생성 실패: 중복된 카테고리명")
    void createIncomeCategoryFail() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        IncomeCategory category = new IncomeCategory(categoryName);

        given(incomeCategoryRepository.findByCategoryName(any())).willReturn(Optional.of(category));

        // when & then
        assertThatThrownBy(() -> categoryService.createIncomeCategory(categoryDto))
                .isInstanceOf(CategoryAlreadyExistException.class);
    }

    @Test
    @DisplayName("지출 카테고리 생성 성공")
    void createExpenseCategory() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);

        given(expenseCategoryRepository.findByCategoryName(any())).willReturn(Optional.empty());
        given(expenseCategoryRepository.save(any())).willReturn(new ExpenseCategory(categoryName));

        // when
        ExpenseCategory saveCategory = categoryService.createExpenseCategory(categoryDto);

        // then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    @DisplayName("지출 카테고리 생성 성공: 삭제된 카테고리 복구")
    void createExpenseCategory2() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        ExpenseCategory category = new ExpenseCategory(categoryName);
        category.delete();

        given(expenseCategoryRepository.findByCategoryName(any())).willReturn(Optional.of(category));

        // when
        ExpenseCategory activeCategory = categoryService.createExpenseCategory(categoryDto);

        // then
        assertThat(activeCategory.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("지출 카테고리 생성 실패: 중복된 카테고리명")
    void createExpenseCategoryFail() {
        // given
        CreateCategoryDto categoryDto = new CreateCategoryDto(categoryName);
        ExpenseCategory category = new ExpenseCategory(categoryName);

        given(expenseCategoryRepository.findByCategoryName(any())).willReturn(Optional.of(category));

        // when & then
        assertThatThrownBy(() -> categoryService.createExpenseCategory(categoryDto))
                .isInstanceOf(CategoryAlreadyExistException.class);
    }

    @Test
    @DisplayName("삭제되지 않은 모든 수입 카테고리 조회")
    void getIncomeCategories() {
        // given
        IncomeCategory category = new IncomeCategory(categoryName);
        List<IncomeCategory> categoryList = new ArrayList<>();
        categoryList.add(category);

        given(incomeCategoryRepository.findAllByIsDeletedFalse()).willReturn(categoryList);

        // when
        List<CategoryResponseDto> incomeCategories = categoryService.getIncomeCategories();

        // then
        assertThat(incomeCategories.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("삭제되지 않은 모든 지출 카테고리 조회")
    void getExpenseCategories() {
        // given
        ExpenseCategory category = new ExpenseCategory(categoryName);
        List<ExpenseCategory> categoryList = new ArrayList<>();
        categoryList.add(category);

        given(expenseCategoryRepository.findAllByIsDeletedFalse()).willReturn(categoryList);

        // when
        List<CategoryResponseDto> expenseCategories = categoryService.getExpenseCategories();

        // then
        assertThat(expenseCategories.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("수입 카테고리 삭제 성공")
    void deleteIncomeCategory() {
        // given
        IncomeCategory category = spy(new IncomeCategory(categoryName));
        given(incomeCategoryRepository.findById(any())).willReturn(Optional.of(category));

        // when
        categoryService.deleteIncomeCategory(1L);

        // then
        verify(incomeCategoryRepository).findById(1L);
        verify(category).delete();
    }

    @Test
    @DisplayName("수입 카테고리 삭제 실패: 존재하지 않는 항목")
    void deleteIncomeCategoryFail() {
        // given
        given(incomeCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteIncomeCategory(1L))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("지출 카테고리 삭제 성공")
    void deleteExpenseCategory() {
        // given
        ExpenseCategory category = spy(new ExpenseCategory(categoryName));
        given(expenseCategoryRepository.findById(any())).willReturn(Optional.of(category));

        // when
        categoryService.deleteExpenseCategory(1L);

        // then
        verify(expenseCategoryRepository).findById(1L);
        verify(category).delete();
    }

    @Test
    @DisplayName("지출 카테고리 삭제 실패: 존재하지 않는 항목")
    void deleteExpenseCategoryFail() {
        // given
        given(expenseCategoryRepository.findById(any())).willThrow(TransactionCategoryNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> categoryService.deleteExpenseCategory(1L))
                .isInstanceOf(TransactionCategoryNotFoundException.class);
    }
}