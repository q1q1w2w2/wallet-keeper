package com.project.wallet_keeper.security.oauth;

import com.project.wallet_keeper.security.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 로그인 성공");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = oAuth2User.getAttribute("provider");
        boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("exist"));
//        String authority = oAuth2User.getAuthorities().stream()
//                .findFirst()
//                .orElseThrow(IllegalStateException::new)
//                .getAuthority();
//
//        String accessToken = null;
//        try {
//            accessToken = tokenProvider.generateAccessToken(email, authority);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8083/api/auth/redirect")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("provider", provider)
                .queryParam("isExist", isExist)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

//        if (isExist) {
//            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8083/api/auth/redirect")
//                    .queryParam("email", email)
//                    .queryParam("name", name)
//                    .queryParam("provider", provider)
//                    .queryParam("isExist", String.valueOf(isExist))
//                    .build()
//                    .encode(StandardCharsets.UTF_8)
//                    .toUriString();
//            getRedirectStrategy().sendRedirect(request, response, targetUrl);
//        } else {
//            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8083/api/auth/redirect")
//                    .queryParam("email", email)
//                    .queryParam("name", name)
//                    .queryParam("provider", provider)
//                    .build()
//                    .encode(StandardCharsets.UTF_8)
//                    .toUriString();
//            getRedirectStrategy().sendRedirect(request, response, targetUrl);
//        }
    }
}
