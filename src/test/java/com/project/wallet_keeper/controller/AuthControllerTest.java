package com.project.wallet_keeper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.OAuthDto;
import com.project.wallet_keeper.dto.auth.RefreshTokenDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.exception.auth.OAuthUserException;
import com.project.wallet_keeper.exception.auth.TokenValidationException;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.security.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import com.project.wallet_keeper.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@WithMockUser
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthController authController;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static String email = "test@email.com";
    private static String password = "test";
    private static String accessToken = "accessToken";
    private static String refreshToken = "refreshToken";


    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        given(authService.login(any())).willReturn(tokenDto);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 아이디 또는 비밀번호")
    void loginFail() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);
        given(authService.login(any())).willThrow(BadCredentialsException.class);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패: OAuth 사용자")
    void loginFail2() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);
        given(authService.login(any())).willThrow(OAuthUserException.class);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());

        verify(authService).logout(any(TokenDto.class));
    }

    @Test
    @DisplayName("로그아웃 실패: 유효하지 않은 토큰")
    void logoutFail() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        doThrow(new TokenValidationException()).when(authService).logout(any(TokenDto.class));

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isUnauthorized());

        verify(authService).logout(any(TokenDto.class));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void refreshToken() throws Exception {
        // given
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);
        given(authService.generateNewAccessTokens(any())).willReturn(accessToken);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 재발급 실패: 유효하지 않은 토큰")
    void refreshTokenFail() throws Exception {
        // given
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);
        given(authService.generateNewAccessTokens(any())).willThrow(TokenValidationException.class);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isUnauthorized());
    }

//    @Test
//    @DisplayName("OAuth 리다이렉트 성공")
//    void redirectForOAuth() throws Exception {
//        // given
//
//        // when
//        ResultActions result = mockMvc.perform(get("/api/auth/redirect")
//                .param("email", email)
//                .param("name", "name")
//                .param("provider", "google")
//                .param("isExist", "true")
//                .with(csrf())
//        );
//
//        // then
//        result.andExpect(status().isOk());
//        result.andExpect(model().attribute("email", email));
//        result.andExpect(view().name("auth/redirect"));
//    }

    @Test
    @DisplayName("OAuth 로그인 성공")
    void getOAuthAccessToken() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        OAuthDto oAuthDto = new OAuthDto(email, "name", "google", "true");
        given(authService.oAuthSignupAndLogin(any())).willReturn(tokenDto);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/oauth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oAuthDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("OAuth 회원가입 실패: 일반 회원가입 한 경우")
    void getOAuthAccessTokenFail() throws Exception {
        // given
        OAuthDto oAuthDto = new OAuthDto(email, "name", "google", "false");
        given(authService.oAuthSignupAndLogin(any())).willThrow(UserAlreadyExistException.class);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/oauth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oAuthDto))
                .with(csrf())
        );

        // then
        result.andExpect(status().isConflict());
    }
}