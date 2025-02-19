package com.project.wallet_keeper.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void update() {
        // given
        User user = createUser();

        // when
        user.update("newNickname", LocalDate.of(2000, 1, 2));

        // then
        assertThat(user.getNickname()).isEqualTo("newNickname");
        assertThat(user.getBirth()).isEqualTo(LocalDate.of(2000, 1, 2));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 성공")
    void updatePassword() {
        // given
        User user = createUser();

        // when
        user.updatePassword("newPassword");

        // then
        assertThat(user.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser() {
        // given
        User user = createUser();

        // when
        user.deleteUser();

        // then
        assertThat(user.isDeleted()).isTrue();
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
}