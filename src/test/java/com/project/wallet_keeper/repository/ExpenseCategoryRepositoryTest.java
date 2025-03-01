package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.ExpenseCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseCategoryRepositoryTest {

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    ExpenseCategory createCategory() {
        return new ExpenseCategory("카테고리1");
    }

    @Test
    @DisplayName("지출 카테고리 저장 성공")
    void save() {
        // given
        ExpenseCategory category = createCategory();

        // when
        ExpenseCategory saveCategory = expenseCategoryRepository.save(category);

        // then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getCategoryName()).isEqualTo("카테고리1");
    }

    @Test
    @DisplayName("지출 카테고리 삭제")
    void deleteCategory() {
        // given
        ExpenseCategory category = createCategory();
        ExpenseCategory saveCategory = expenseCategoryRepository.save(category);

        // when
        saveCategory.delete();

        // then
        assertThat(saveCategory.isDeleted()).isTrue();
    }


    @Test
    @DisplayName("삭제되지 않은 지출 카테고리 조회 성공")
    void findAllByIsDeletedFalse() {
        // given
        ExpenseCategory category = createCategory();
        ExpenseCategory category2 = createCategory();
        ExpenseCategory saveCategory = expenseCategoryRepository.save(category);
        ExpenseCategory saveCategory2 = expenseCategoryRepository.save(category2);
        saveCategory2.delete();

        // when
        List<ExpenseCategory> result = expenseCategoryRepository.findAllByIsDeletedFalse();

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}