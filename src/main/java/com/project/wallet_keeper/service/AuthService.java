package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.RefreshTokenDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.exception.TokenValidationException;
import com.project.wallet_keeper.exception.UserNotFoundException;
import com.project.wallet_keeper.repository.UserRepository;
import com.project.wallet_keeper.security.jwt.TokenProvider;
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

import java.util.stream.Collectors;

import static com.project.wallet_keeper.security.jwt.TokenProvider.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    @Transactional
    public TokenDto login(LoginDto loginDto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        // authenticationManager 내부적으로 UserDetailsService 호출
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
        return tokenProvider.generateAccessToken(subject, user.getRole());
    }

}
