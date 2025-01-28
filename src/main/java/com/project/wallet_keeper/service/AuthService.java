package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.OAuthDto;
import com.project.wallet_keeper.dto.auth.RefreshTokenDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.exception.auth.OAuthUserException;
import com.project.wallet_keeper.exception.auth.TokenValidationException;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotActiveException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import com.project.wallet_keeper.repository.UserRepository;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.wallet_keeper.entity.Role.ROLE_USER;
import static com.project.wallet_keeper.util.jwt.TokenProvider.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    @Transactional
    public TokenDto login(LoginDto loginDto) throws Exception {
        isOAuthUser(loginDto.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String email = loginDto.getEmail();
        String authority = authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = tokenProvider.generateAccessToken(email, authority);
        String refreshToken = tokenProvider.generateRefreshToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    private void isOAuthUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isPresent()) {
            User user = findUser.get();
            checkDeactivated(user);

            if (user.getProvider() != null || user.getPassword() == null) {
                throw new OAuthUserException("소셜 로그인을 이용해주세요.");
            }
        }
    }

    private void checkDeactivated(User user) {
        if (user.isDeleted()) {
            throw new UserNotActiveException();
        }
    }

    @Transactional
    public void logout(TokenDto tokenDto) throws Exception {
        String refreshToken = tokenDto.getRefreshToken();
        String accessToken = tokenDto.getAccessToken();
        if (!tokenProvider.validateToken(accessToken) || !tokenProvider.validateToken(refreshToken)) {
            throw new TokenValidationException();
        }

        String email = tokenProvider.extractEmailFromToken(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + email;

        Boolean hasKey = redisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
            log.info("로그아웃 되었습니다.");
        } else {
            throw new TokenValidationException("토큰이 존재하지 않습니다.");
        }
    }

    @Transactional
    public String generateNewAccessTokens(RefreshTokenDto tokenDto) throws Exception {
        String refreshToken = tokenDto.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenValidationException();
        }

        String subject = tokenProvider.extractEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(subject)
                .orElseThrow(UserNotFoundException::new);

        return tokenProvider.generateAccessToken(subject, user.getRole().toString());
    }

    @Transactional
    public TokenDto generateNewTokens(RefreshTokenDto tokenDto) throws Exception {
        String refreshToken = tokenDto.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenValidationException();
        }

        String subject = tokenProvider.extractEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(subject)
                .orElseThrow(UserNotFoundException::new);

        String newAccessToken = tokenProvider.generateAccessToken(subject, user.getRole().toString());
        String newRefreshToken = tokenProvider.generateRefreshToken(subject);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public TokenDto oAuthSignupAndLogin(OAuthDto oAuthDto) throws Exception {
        String email = oAuthDto.getEmail();
        String name = oAuthDto.getName();
        String provider = oAuthDto.getProvider();
        boolean isExist = Boolean.parseBoolean(oAuthDto.getIsExist());

        User user = null;
        if (!isExist) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new UserAlreadyExistException("이미 가입되어 있는 이메일입니다.");
            }

            User saveUser = User.builder()
                    .email(email)
                    .nickname(name)
                    .birth(LocalDate.now())
                    .role(ROLE_USER)
                    .provider(provider)
                    .build();
            user = userRepository.save(saveUser);
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(UserNotFoundException::new);
        }

        String accessToken = tokenProvider.generateAccessToken(email, user.getRole().toString());
        String refreshToken = tokenProvider.generateRefreshToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

}
