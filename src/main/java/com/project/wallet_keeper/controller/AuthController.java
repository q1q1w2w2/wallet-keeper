package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.dto.auth.*;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "AuthController", description = "인증 컨트롤러 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        TokenDto tokens = authService.login(loginDto);

        ApiResponse<TokenDto> response = ApiResponse.success(OK, "로그인 되었습니다.", tokens);
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<TokenDto>> logout(@Valid @RequestBody TokenDto tokenDto) throws Exception {
        authService.logout(tokenDto);

        ApiResponse<TokenDto> response = ApiResponse.success(OK, "로그아웃 되었습니다.");
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refreshToken(@Valid @RequestBody RefreshTokenDto tokenDto) throws Exception {
        TokenDto tokens = authService.generateNewTokens(tokenDto);

        ApiResponse<TokenDto> response = ApiResponse.success(OK, "토큰이 재발급 되었습니다.", tokens);
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/oauth")
    public ResponseEntity<ApiResponse<TokenDto>> getOAuthAccessToken(@RequestBody OAuthDto oAuthDto) throws Exception {
        TokenDto tokens = authService.oAuthSignupAndLogin(oAuthDto);

        ApiResponse<TokenDto> response = ApiResponse.success(OK, "로그인 되었습니다.", tokens);
        return ResponseEntity.status(OK).body(response);
    }
}
