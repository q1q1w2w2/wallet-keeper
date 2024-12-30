package com.project.wallet_keeper.service;

import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.exception.TokenValidationException;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.wallet_keeper.security.jwt.TokenProvider.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ROLE_USER = "ROLE_USER";

    @Transactional
    public TokenDto login(LoginDto loginDto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        // authenticationManager 내부적으로 UserDetailsService 호출
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String accessToken = tokenProvider.generateAccessToken(loginDto.getEmail(), ROLE_USER);
        String refreshToken = tokenProvider.generateRefreshToken(loginDto.getEmail());
        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public void logout(TokenDto tokenDto) throws Exception {
        if (!tokenProvider.validateToken(tokenDto.getAccessToken()) || !tokenProvider.validateToken(tokenDto.getRefreshToken())) {
            throw new TokenValidationException();
        }

        String email = tokenProvider.extractEmailFromToken(tokenDto.getRefreshToken());
        String redisKey = REFRESH_TOKEN_PREFIX + email;

        Boolean hasKey = redisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
            log.info("로그아웃 되었습니다.");
        } else {
            throw new TokenValidationException("토큰이 존재하지 않습니다.");
        }
    }

}
