package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.wallet_keeper.domain.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExpenseCategoryRepository expenseCategoryRepository;

    User user;
    ExpenseCategory category;

    private static final String DETAIL = "식비";
    private static final Integer AMOUNT = 10000;
    private static final String DESCRIPTION = "설명";
    private static final LocalDateTime EXPENSE_AT = LocalDateTime.of(2000, 1, 1, 0, 0);


    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@email.com")
                .password("password")
                .nickname("사용자")
                .birth(LocalDate.of(2000, 1, 1))
                .role(ROLE_USER)
                .build());
        category = expenseCategoryRepository.save(ExpenseCategory.builder()
                .categoryName("급여")
                .build()
        );
    }

    Expense createExpense() {
        return Expense.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .expenseAt(EXPENSE_AT)
                .description(DESCRIPTION)
                .expenseCategory(category)
                .build();
    }

    @Test
    @DisplayName("지출 저장 성공")
    void saveExpense() {
        // given
        Expense expense = createExpense();

        // when
        Expense saveExpense = expenseRepository.save(expense);

        // then
        assertThat(saveExpense).isNotNull();
        assertThat(saveExpense.getDetail()).isEqualTo(DETAIL);
        assertThat(saveExpense.getAmount()).isEqualTo(AMOUNT);
        assertThat(saveExpense.getUser()).isEqualTo(user);
        assertThat(saveExpense.getExpenseAt()).isEqualTo(EXPENSE_AT);
        assertThat(saveExpense.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(saveExpense.getExpenseCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("사용자에 대한 지출 목록 조회")
    void findByUser() {
        // given
        Expense expense = createExpense();
        Expense saveExpense = expenseRepository.save(expense);

        // when
        List<Expense> expenseList = expenseRepository.findByUser(user);

        // then
        assertThat(expenseList.size()).isEqualTo(1);
        assertThat(expenseList).contains(saveExpense);
    }

    @Test
    @DisplayName("지출 삭제 성공")
    void deleteIncome() {
        // given
        Expense expense = createExpense();
        Expense saveExpense = expenseRepository.save(expense);

        // when
        expenseRepository.delete(saveExpense);
        List<Expense> expenseList = expenseRepository.findByUser(user);

        // then
        assertThat(expenseList).isEmpty();
    }

    @Test
    @DisplayName("지출 업데이트 성공")
    void updateExpense() {
        // given
        Expense expense = createExpense();
        Expense saveExpense = expenseRepository.save(expense);

        // when
        saveExpense.update("updateDetail", 20000, "updateDescription", EXPENSE_AT, category);

        // then
        assertThat(saveExpense.getDetail()).isEqualTo("updateDetail");
        assertThat(saveExpense.getAmount()).isEqualTo(20000);
        assertThat(saveExpense.getDescription()).isEqualTo("updateDescription");
    }
}