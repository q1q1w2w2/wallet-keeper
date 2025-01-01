package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.Role;
import com.project.wallet_keeper.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.project.wallet_keeper.domain.Role.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    final String EMAIL = "test@gmail.com";
    final String PASSWORD = "password";
    final String NICKNAME = "사용자01";
    final LocalDate BIRTH = LocalDate.of(2000, 1, 1);

    @Test
    @DisplayName("사용자 정보 저장: 성공")
    void save() {
        // given & when
        User user = createAndSaveUser();

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
        assertThat(user.getNickname()).isEqualTo(NICKNAME);
        assertThat(user.getBirth()).isEqualTo(BIRTH);
        assertThat(user.getRole()).isEqualTo(ROLE_USER);
        assertThat(user.getProvider()).isNull();
    }

    @Test
    @DisplayName("사용자 정보 저장: 실패(email이 null일 경우)")
    void save_fail_withoutEmail() {
        // given
        User userWithoutEmail = createUser(null, PASSWORD, NICKNAME, BIRTH, ROLE_USER);

        // when & then
        assertThatThrownBy(() -> userRepository.save(userWithoutEmail))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("사용자 정보 저장: 실패(nickname이 null일 경우)")
    void save_fail_withoutNickname() {
        // given
        User userWithoutNickname = createUser(EMAIL, PASSWORD, null, BIRTH, ROLE_USER);

        // when & then
        assertThatThrownBy(() -> userRepository.save(userWithoutNickname))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("사용자 정보 저장: 실패(birth가 null일 경우)")
    void save_fail_withoutBirth() {
        // given
        User userWithoutBirth = createUser(EMAIL, PASSWORD, NICKNAME, null, ROLE_USER);

        // when & then
        assertThatThrownBy(() -> userRepository.save(userWithoutBirth))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("사용자 정보 저장: 실패(role이 null일 경우)")
    void save_fail_withoutRole() {
        // given
        User userWithoutRole = createUser(EMAIL, PASSWORD, NICKNAME, BIRTH, null);

        // when & then
        assertThatThrownBy(() -> userRepository.save(userWithoutRole))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일로 사용자 찾기: 성공")
    void findByEmail() {
        // given
        createAndSaveUser();

        // when
        Optional<User> findUser = userRepository.findByEmail(EMAIL);

        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("이메일로 사용자 찾기: 실패")
    void findByEmail_fail() {
        // given
        createAndSaveUser();

        // when
        Optional<User> findUser = userRepository.findByEmail(EMAIL + "other");

        // then
        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("닉네임으로 사용자 찾기: 성공")
    void findByNickname() {
        // given
        createAndSaveUser();

        // when
        Optional<User> findUser = userRepository.findByNickname(NICKNAME);

        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getNickname()).isEqualTo(NICKNAME);
    }

    @Test
    @DisplayName("닉네임으로 사용자 찾기: 실패")
    void findByNickname_fail() {
        // given & when
        Optional<User> findUser = userRepository.findByNickname(NICKNAME + "other");

        // then
        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("사용자 삭제: 성공")
    void delete() {
        // given
        User user = createAndSaveUser();

        // then
        userRepository.delete(user);

        // then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 사용자 찾기: 성공")
    void findAll() {
        // given
        createAndSaveUser();

        // when
        List<User> userList = userRepository.findAll();

        // then
        assertThat(userList.size()).isEqualTo(1);
    }

    User createAndSaveUser() {
        return userRepository.save(
                User.builder()
                        .email(EMAIL)
                        .password(PASSWORD)
                        .nickname(NICKNAME)
                        .birth(BIRTH)
                        .role(ROLE_USER)
                        .build());
    }

    User createUser(String email, String password, String nickname, LocalDate birth, Role role) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .birth(birth)
                .role(role)
                .build();
    }
}