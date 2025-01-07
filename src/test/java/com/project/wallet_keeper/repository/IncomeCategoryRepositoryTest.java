package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.IncomeCategory;
import com.project.wallet_keeper.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("IncomeCategoryRepository 테스트")
class IncomeCategoryRepositoryTest {

    @Autowired
    private IncomeCategoryRepository incomeCategoryRepository;

    IncomeCategory createCategory() {
        return new IncomeCategory("카테고리1");
    }

    @Test
    @DisplayName("수입 카테고리 저장 성공")
    void save() {
        // given
        IncomeCategory category = createCategory();

        // when
        IncomeCategory saveCategory = incomeCategoryRepository.save(category);

        // then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getCategoryName()).isEqualTo("카테고리1");
    }

    @Test
    @DisplayName("수입 카테고리 삭제")
    void deleteCategory() {
        // given
        IncomeCategory category = createCategory();
        IncomeCategory saveCategory = incomeCategoryRepository.save(category);

        // when
        saveCategory.delete();

        // then
        assertThat(saveCategory.isDeleted()).isTrue();
    }


    @Test
    @DisplayName("삭제되지 않은 수입 카테고리 조회 성공")
    void findAllByIsDeletedFalse() {
        // given
        IncomeCategory category = createCategory();
        IncomeCategory category2 = createCategory();
        IncomeCategory saveCategory = incomeCategoryRepository.save(category);
        IncomeCategory saveCategory2 = incomeCategoryRepository.save(category2);
        saveCategory2.delete();

        // when
        List<IncomeCategory> result = incomeCategoryRepository.findAllByIsDeletedFalse();

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}