package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.Income;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.wallet_keeper.entity.Role.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class IncomeRepositoryTest {

    @Autowired
    IncomeRepository incomeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IncomeCategoryRepository incomeCategoryRepository;

    User user;
    IncomeCategory category;

    private static final String DETAIL = "월급";
    private static final Integer AMOUNT = 10000;
    private static final String DESCRIPTION = "설명";
    private static final LocalDateTime INCOME_AT = LocalDateTime.of(2000, 1, 1, 0, 0);


    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@email.com")
                .password("password")
                .nickname("사용자")
                .birth(LocalDate.of(2000, 1, 1))
                .role(ROLE_USER)
                .build());
        category = incomeCategoryRepository.save(IncomeCategory.builder()
                .categoryName("급여")
                .build()
        );
    }

    Income createIncome() {
        return Income.builder()
                .detail(DETAIL)
                .amount(AMOUNT)
                .user(user)
                .incomeAt(INCOME_AT)
                .description(DESCRIPTION)
                .incomeCategory(category)
                .build();
    }

    @Test
    @DisplayName("수입 저장 성공")
    void saveIncome() {
        // given
        Income income = createIncome();

        // when
        Income saveIncome = incomeRepository.save(income);

        // then
        assertThat(saveIncome).isNotNull();
        assertThat(saveIncome.getDetail()).isEqualTo(DETAIL);
        assertThat(saveIncome.getAmount()).isEqualTo(AMOUNT);
        assertThat(saveIncome.getUser()).isEqualTo(user);
        assertThat(saveIncome.getIncomeAt()).isEqualTo(INCOME_AT);
        assertThat(saveIncome.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(saveIncome.getIncomeCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("사용자에 대한 수입 목록 조회")
    void findByUser() {
        // given
        Income income = createIncome();
        Income saveIncome = incomeRepository.save(income);

        // when
        List<Income> incomeList = incomeRepository.findByUser(user);

        // then
        assertThat(incomeList.size()).isEqualTo(1);
        assertThat(incomeList).contains(saveIncome);
    }

    @Test
    @DisplayName("수입 삭제 성공")
    void deleteIncome() {
        // given
        Income income = createIncome();
        Income saveIncome = incomeRepository.save(income);

        // when
        incomeRepository.delete(saveIncome);
        List<Income> incomeList = incomeRepository.findByUser(user);

        // then
        assertThat(incomeList).isEmpty();
    }

    @Test
    @DisplayName("수입 업데이트 성공")
    void updateExpense() {
        // given
        Income income = createIncome();
        Income saveIncome = incomeRepository.save(income);

        // when
        saveIncome.update("updateDetail", 20000, "updateDescription", INCOME_AT, category);

        // then
        assertThat(saveIncome.getDetail()).isEqualTo("updateDetail");
        assertThat(saveIncome.getAmount()).isEqualTo(20000);
        assertThat(saveIncome.getDescription()).isEqualTo("updateDescription");
    }

}