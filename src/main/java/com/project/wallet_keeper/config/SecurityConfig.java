package com.project.wallet_keeper.config;

import com.project.wallet_keeper.util.auth.CustomAccessDeniedHandler;
import com.project.wallet_keeper.util.auth.CustomAuthenticationEntryPoint;
import com.project.wallet_keeper.util.auth.CustomUserDetailsService;
import com.project.wallet_keeper.util.jwt.JwtFilter;
import com.project.wallet_keeper.util.jwt.TokenProvider;
import com.project.wallet_keeper.util.oauth.CustomAuthenticationFailureHandler;
import com.project.wallet_keeper.util.oauth.CustomAuthenticationSuccessHandler;
import com.project.wallet_keeper.util.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }   

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers("/error", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**", "/static/js/**").permitAll()
                                .requestMatchers("/login", "/signup", "/", "/calendar", "/setting", "/budget", "/summary").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                                .requestMatchers("/api/auth/login", "/api/auth/redirect", "/api/auth/oauth").permitAll()
                                .requestMatchers("/api/users").permitAll()
                                .requestMatchers("/api/mail/verification", "/api/mail/verification/code").permitAll()
                                .requestMatchers("/api/users/reset-password").permitAll()
                                .requestMatchers("/actuator/health").permitAll()

                                .requestMatchers("/ws/notification").permitAll()
                                .requestMatchers("/ws/notification/**").permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .addFilterBefore(new JwtFilter(tokenProvider, customAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )

                .oauth2Login(oauth ->
                        oauth
                                .userInfoEndpoint(userInfo ->
                                        userInfo
                                                .userService(customOAuth2UserService)
                                )
                                .successHandler(customAuthenticationSuccessHandler)
                                .failureHandler(customAuthenticationFailureHandler)
                )
        ;


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedOrigins(List.of("http://localhost:8083"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
        config.addExposedHeader(JwtFilter.AUTHORIZATION_HEADER);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
