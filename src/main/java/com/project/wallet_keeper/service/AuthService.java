package com.project.wallet_keeper.service;

import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final String ROLE_USER = "ROLE_USER";

    public TokenDto login(LoginDto dto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        // authenticationManager 내부적으로 UserDetailsService 호출
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String accessToken = tokenProvider.generateAccessToken(dto.getEmail(), ROLE_USER);
        String refreshToken = tokenProvider.generateRefreshToken(dto.getEmail());
        return new TokenDto(accessToken, refreshToken);
    }

}
