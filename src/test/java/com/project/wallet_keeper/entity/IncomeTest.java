package com.project.wallet_keeper.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class IncomeTest {

    @Test
    @DisplayName("수입 항목 수정 성공")
    void update() {
        // given
        User user = createUser();
        IncomeCategory category = createCategory();
        Income income = Income.builder()
                .detail("수입")
                .amount(10000)
                .description("")
                .user(user)
                .incomeAt(LocalDateTime.of(2000, 1, 1, 0, 0))
                .incomeCategory(category)
                .build();

        // when
        income.update("수정수입", 20000, "수정설명", LocalDateTime.of(2000, 1, 1, 0, 0), category);

        // then
        assertThat(income.getDetail()).isEqualTo("수정수입");
        assertThat(income.getAmount()).isEqualTo(20000);
        assertThat(income.getDescription()).isEqualTo("수정설명");
    }

    private static User createUser() {
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .nickname("닉네임")
                .birth(LocalDate.of(2000, 1, 1))
                .role(Role.ROLE_USER)
                .build();
        return user;
    }

    private static IncomeCategory createCategory() {
        return new IncomeCategory("카테고리");
    }
}