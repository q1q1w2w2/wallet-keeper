package com.project.wallet_keeper.util.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 로그인 성공");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = oAuth2User.getAttribute("provider");
        boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("exist"));

        String targetUrl = UriComponentsBuilder.fromUriString(baseUrl + "/api/auth/redirect")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("provider", provider)
                .queryParam("isExist", isExist)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
