package com.project.wallet_keeper.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.security.auth.CustomAuthenticationEntryPoint;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                if (tokenProvider.validateToken(token)) {
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Security Context에 '{}' 인증 정보 저장됨, URI: {}", authentication.getName(), request.getRequestURI());
                } else {
                    handleTokenError(response, "유효한 인증이 없습니다.");
                    return;
                }
            } catch (ExpiredJwtException e) {
                handleTokenError(response, "만료된 토큰입니다.");
                return;
            } catch (AuthenticationException e) {
                authenticationEntryPoint.commence(request, response, e);
                return;
            } catch (Exception e) {
                log.error("JWT 처리 중 오류 발생: {}", e.getMessage());
                handleTokenError(response, "인증 처리 중 오류가 발생했습니다.");
                return;
            }
        } else {
            log.info("Authorization 헤더 없음, URI: {}", request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }


    private void handleTokenError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED, message, null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), apiResponse);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
