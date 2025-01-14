package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.Role;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.OAuthDto;
import com.project.wallet_keeper.dto.auth.RefreshTokenDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.exception.auth.OAuthUserException;
import com.project.wallet_keeper.exception.auth.TokenValidationException;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import com.project.wallet_keeper.repository.UserRepository;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private UserRepository userRepository;;

    private static String email = "test@email.com";
    private static String password = "test";
    private static String accessToken = "accessToken";
    private static String refreshToken = "refreshToken";

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);

        User user = createUser();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(authenticationManager.authenticate(any())).willReturn(mock(Authentication.class));
        given(tokenProvider.generateAccessToken(anyString(), anyString())).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken(anyString())).willReturn(refreshToken);

        // when
        TokenDto tokenDto = authService.login(loginDto);

        // then
        assertThat(tokenDto).isNotNull();
        assertThat(tokenDto.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenDto.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 이메일")
    void loginFail() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(authenticationManager.authenticate(any())).willThrow(BadCredentialsException.class);

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);
    }


    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호")
    void loginFail2() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);
        User user = createUser();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(authenticationManager.authenticate(any())).willThrow(BadCredentialsException.class);

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("로그인 실패: OAuth 가입 유저")
    void loginFail3() throws Exception {
        // given
        LoginDto loginDto = new LoginDto(email, password);
        User user = User.builder()
                .email(email)
                .password(null)
                .birth(LocalDate.now())
                .role(Role.ROLE_USER)
                .provider("google")
                .nickname("사용자")
                .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(OAuthUserException.class);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        given(tokenProvider.validateToken(any())).willReturn(true);
        given(tokenProvider.extractEmailFromToken(any())).willReturn(email);
        given(redisTemplate.hasKey(any())).willReturn(true);

        // when
        authService.logout(tokenDto);

        // then
        verify(redisTemplate).delete(anyString());
    }

    @Test
    @DisplayName("로그아웃 실패: 유효하지 않은 토큰")
    void logoutFail() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        given(tokenProvider.validateToken(any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.logout(tokenDto))
                .isInstanceOf(TokenValidationException.class);
    }

    @Test
    @DisplayName("로그아웃 실패: 존재하지 않는 토큰")
    void logoutFail2() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        given(tokenProvider.validateToken(any())).willReturn(true);
        given(tokenProvider.extractEmailFromToken(any())).willReturn(email);
        given(redisTemplate.hasKey(any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.logout(tokenDto))
                .isInstanceOf(TokenValidationException.class);
    }

    @Test
    @DisplayName("accessToken 재발급 성공")
    void generateNewAccessTokens() throws Exception {
        // given
        User user = createUser();
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);

        given(tokenProvider.validateToken(any())).willReturn(true);
        given(tokenProvider.extractEmailFromToken(any())).willReturn(email);
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(tokenProvider.generateAccessToken(anyString(), anyString())).willReturn(accessToken);

        // when
        String newAccessToken = authService.generateNewAccessTokens(refreshTokenDto);

        // then
        assertThat(newAccessToken).isNotNull();
    }

    @Test
    @DisplayName("accessToken 재발급 실패: 유효하지 않은 refreshToken")
    void generateNewAccessTokensFail() throws Exception {
        // given
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);

        given(tokenProvider.validateToken(any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.generateNewAccessTokens(refreshTokenDto))
                .isInstanceOf(TokenValidationException.class);
    }

    @Test
    @DisplayName("accessToken 재발급 실패: 존재하지 않는 이메일")
    void generateNewAccessTokensFail2() throws Exception {
        // given
        User user = createUser();
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);

        given(tokenProvider.validateToken(any())).willReturn(true);
        given(tokenProvider.extractEmailFromToken(any())).willReturn(email);
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.generateNewAccessTokens(refreshTokenDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("OAuth 회원 가입 및 토큰 발급 성공")
    void oAuthSignupAndLogin() throws Exception {
        // given
        User user = createUser();
        OAuthDto oAuthDto = new OAuthDto(email, "새로운사용자", "google", "false");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(user);
        given(tokenProvider.generateAccessToken(anyString(), anyString())).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken(anyString())).willReturn(refreshToken);

        // when
        TokenDto tokenDto = authService.oAuthSignupAndLogin(oAuthDto);

        // then
        assertThat(tokenDto).isNotNull();
    }

    @Test
    @DisplayName("OAuth 회원 가입 실패: 이미 가입된 사용자")
    void oAuthSignupAndLoginFail() throws Exception {
        // given
        User user = createUser();
        OAuthDto oAuthDto = new OAuthDto(email, "새로운사용자", "google", "false");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.oAuthSignupAndLogin(oAuthDto))
                .isInstanceOf(UserAlreadyExistException.class);
    }

    User createUser() {
        return User.builder()
                .email(email)
                .password(password)
                .birth(LocalDate.now())
                .role(Role.ROLE_USER)
                .nickname("사용자")
                .build();
    }
}