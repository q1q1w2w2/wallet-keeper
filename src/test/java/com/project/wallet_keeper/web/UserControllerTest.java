package com.project.wallet_keeper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.domain.Reason;
import com.project.wallet_keeper.domain.Role;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.*;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import com.project.wallet_keeper.security.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import com.project.wallet_keeper.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserController userController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @DisplayName("회원가입 성공")
    void signUp() throws Exception {
        // given
        User user = createUser();
        SignupDto signupDto = createSignupDto();

        given(userService.signUp(any())).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    @DisplayName("회원가입 실패: 이메일 중복")
    void signUpEmailAlreadyExist() throws Exception {
        // given
        SignupDto signupDto = createSignupDto();
        given(userService.signUp(any())).willThrow(UserAlreadyExistException.class);

        // when
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isConflict());
//                .andExpect(jsonPath("$.message").value("이미 가입되어 있는 이메일입니다."));
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보 조회 성공")
    void getCurrentUser() throws Exception {
        // given
        User currentUser = createUser();
        given(userService.getCurrentUser()).willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currentUser))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(currentUser.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(currentUser.getNickname()))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보 조회 실패: 사용자 없음")
    void getCurrentUserNotFound() throws Exception {
        // given
        User currentUser = createUser();
        given(userService.getCurrentUser()).willThrow(UserNotFoundException.class);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currentUser))
                .with(csrf())
        );

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보 수정 성공")
    void updateCurrentUser() throws Exception {
        // given
        User currentUser = createUser();
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto("changeNickname", LocalDate.of(2000, 1, 1));

        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.updateUser(eq(currentUser), any(UserProfileUpdateDto.class)))
                .willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(currentUser.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(currentUser.getNickname()))
                .andExpect(jsonPath("$.message").value("사용자 정보가 수정되었습니다."));
    }

    @Test
    @DisplayName("현재 로그인한 사용자 정보 수정 실패: 빈 닉네임")
    void updateCurrentUserInvalidInput() throws Exception {
        // given
        User currentUser = createUser();
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto("", LocalDate.of(2000, 1, 1));

        given(userService.getCurrentUser()).willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("현재 로그인한 사용자 탈퇴 성공")
    void deleteCurrentUser() throws Exception {
        // given
        User currentUser = createUser();
        ReasonDto reasonDto = new ReasonDto("탈퇴사유");

        given(userService.getCurrentUser()).willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me/status")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reasonDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."));

        verify(userService).deleteUser(eq(currentUser), anyString()); // 반환 타입이 void이므로 호출되었는지 검증
    }

    @Test
    @DisplayName("로그인한 사용자 비밀번호 변경 성공")
    void updatePassword() throws Exception {
        // given
        User currentUser = createUser();
        UpdatePasswordDto passwordDto = new UpdatePasswordDto("oldPassword", "newPassword");

        given(userService.getCurrentUser()).willReturn(currentUser);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me/password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다."));

        verify(userService).updatePassword(eq(currentUser), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("로그인한 사용자 비밀번호 변경 실패: 현재 비밀번호 불일치")
    void updatePasswordWrongOldPassword() throws Exception {
        // given
        User currentUser = createUser();
        UpdatePasswordDto passwordDto = new UpdatePasswordDto("wrongOldPassword", "newPassword");

        given(userService.getCurrentUser()).willReturn(currentUser);
        doThrow(BadCredentialsException.class)
                .when(userService).updatePassword(eq(currentUser), any(UpdatePasswordDto.class));

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/me/password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isUnauthorized());

        verify(userService).updatePassword(eq(currentUser), any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("비밀번호 초기화 성공")
    void resetPassword() throws Exception {
        // given
        User currentUser = createUser();
        ResetPasswordDto passwordDto = new ResetPasswordDto("test@email.com", "newPassword");

        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.resetPassword(any(ResetPasswordDto.class)))
                .willReturn(true);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/reset-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다."));
    }

    @Test
    @DisplayName("비밀번호 초기화 실패: 존재하지 않는 이메일")
    void resetPasswordEmailNotFound() throws Exception {
        // given
        User currentUser = createUser();
        ResetPasswordDto passwordDto = new ResetPasswordDto("test@email.com", "newPassword");

        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.resetPassword(any(ResetPasswordDto.class)))
                .willThrow(UserNotFoundException.class);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/reset-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isNotFound());
    }

    SignupDto createSignupDto() {
        return new SignupDto("email@email.com", "password", "사용자01", LocalDate.of(2000, 1, 1));
    }

    User createUser() {
        return User.builder()
                .email("email@email.com")
                .password("password")
                .nickname("사용자01")
                .role(Role.ROLE_USER)
                .birth(LocalDate.of(2000, 1, 1))
                .build();
    }
}