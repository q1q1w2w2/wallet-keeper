package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.Reason;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.user.ResetPasswordDto;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.dto.user.UpdatePasswordDto;
import com.project.wallet_keeper.dto.user.UserProfileUpdateDto;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import com.project.wallet_keeper.repository.ReasonRepository;
import com.project.wallet_keeper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static com.project.wallet_keeper.entity.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ReasonRepository reasonRepository;

    @Mock
    private User mockUser;

    final String EMAIL = "test@email.com";
    final String NICKNAME = "사용자01";
    final LocalDate BIRTH = LocalDate.of(2000, 1, 1);

    SignupDto createSignupDto() {
        return new SignupDto(EMAIL, "password", NICKNAME, BIRTH);
    }

    User createUser() {
        return User.builder()
                .email(EMAIL)
                .password("password")
                .nickname(NICKNAME)
                .birth(BIRTH)
                .role(ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp() {
        // given
        SignupDto signupDto = createSignupDto();

        given(userRepository.findByEmail(signupDto.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(signupDto.getPassword())).willReturn("encodedPassword");
        User user = User.builder()
                .email(signupDto.getEmail())
                .password("encodedPassword")
                .nickname(signupDto.getNickname())
                .birth(signupDto.getBirth())
                .role(ROLE_USER)
                .build();
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User signupUser = userService.signUp(signupDto);

        // then
        assertThat(signupUser).isNotNull();
        assertThat(signupUser.getEmail()).isEqualTo(EMAIL);
        assertThat(signupUser.getNickname()).isEqualTo(NICKNAME);

        verify(userRepository).findByEmail(signupDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패: 중복된 이메일")
    void signUpAlreadyExistEmail() {
        // given
        SignupDto signupDto = createSignupDto();
        User user = User.builder()
                .email(signupDto.getEmail())
                .password("password")
                .nickname(signupDto.getNickname())
                .birth(signupDto.getBirth())
                .role(ROLE_USER)
                .build();

        given(userRepository.findByEmail(signupDto.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.signUp(signupDto))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("이미 가입되어 있는 이메일입니다.");

        verify(userRepository).findByEmail(signupDto.getEmail());
        verify(userRepository, never()).save(any(User.class)); // 호출되면 안됨
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser() {
        // given
        User user = createUser();

        LocalDate newBirth = LocalDate.of(2000, 12, 31);
        String newNickname = "newNickname";
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto(newNickname, newBirth);

        // when
        User updateUser = userService.updateUser(user, updateDto);

        // given
        assertThat(updateUser).isNotNull();
        assertThat(updateUser.getEmail()).isEqualTo(EMAIL);
        assertThat(updateUser.getNickname()).isEqualTo(newNickname);
        assertThat(updateUser.getBirth()).isEqualTo(newBirth);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser() {
        // given
        Reason reason = new Reason("탈퇴사유");

        given(reasonRepository.save(any())).willReturn(reason);

        // when
        userService.deleteUser(mockUser, "탈퇴사유");

        // then
        verify(mockUser).deleteUser();
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 성공")
    void updatePassword() {
        // given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        UpdatePasswordDto passwordDto = new UpdatePasswordDto(oldPassword, newPassword);

        String encodeOldPassword = passwordEncoder.encode(oldPassword);
        boolean matchesResult = true;

        given(mockUser.getPassword()).willReturn(encodeOldPassword);
        given(passwordEncoder.matches(oldPassword, mockUser.getPassword())).willReturn(matchesResult);

        // when
        userService.updatePassword(mockUser, passwordDto);

        // then
        verify(passwordEncoder).matches(oldPassword, mockUser.getPassword());
        verify(mockUser).updatePassword(passwordEncoder.encode(newPassword));
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 실패: 비밀번호 불일치")
    void updatePasswordFailure() {
        // given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        UpdatePasswordDto passwordDto = new UpdatePasswordDto(oldPassword, newPassword);

        String wrongPassword = passwordEncoder.encode("wrongPassword");
        boolean matchesResult = false;
        given(mockUser.getPassword()).willReturn(wrongPassword);
        given(passwordEncoder.matches(oldPassword, mockUser.getPassword())).willReturn(matchesResult);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(mockUser, passwordDto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPasswordSuccess() {
        // given
        String email = "test@example.com";
        String newPassword = "newPassword";
        ResetPasswordDto passwordDto = new ResetPasswordDto(email, newPassword);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.encode(newPassword)).willReturn("encodedPassword");

        // when
        boolean result = userService.resetPassword(passwordDto);

        // then
        assertThat(result).isTrue();
        verify(mockUser).updatePassword("encodedPassword");
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("비밀번호 재설정 실패: 사용자 없음")
    void resetPasswordUserNotFound() {
        // given
        String email = "test@example.com";
        ResetPasswordDto passwordDto = new ResetPasswordDto(email, "newPassword");

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.resetPassword(passwordDto))
                .isInstanceOf(UserNotFoundException.class);
    }
}