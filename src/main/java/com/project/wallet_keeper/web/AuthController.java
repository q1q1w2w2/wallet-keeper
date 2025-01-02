package com.project.wallet_keeper.web;

import com.project.wallet_keeper.dto.auth.*;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Controller
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
    public ResponseEntity<ApiResponse<AccessTokenDto>> refreshToken(@Valid @RequestBody RefreshTokenDto tokenDto) throws Exception {
        String newAccessToken = authService.generateNewAccessTokens(tokenDto);

        AccessTokenDto data = new AccessTokenDto(newAccessToken);
        ApiResponse<AccessTokenDto> response = ApiResponse.success(OK, "토큰이 재발급 되었습니다.", data);
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/redirect")
    public String redirectForOAuth(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String provider,
            @RequestParam boolean isExist,
            Model model
            ) {
        log.info("email: {}, name: {}, provider: {}, isExist: {}", email, name, provider, isExist);

        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("provider", provider);
        model.addAttribute("isExist", isExist);
        return "auth/redirect";
    }

    @PostMapping("/oauth")
    public ResponseEntity<ApiResponse<TokenDto>> getAccessToken(@RequestBody OAuthDto oAuthDto) throws Exception {
        TokenDto tokens = authService.oAuthSignupAndLogin(oAuthDto);

        ApiResponse<TokenDto> response = ApiResponse.success(OK, "로그인 되었습니다.", tokens);
        return ResponseEntity.status(OK).body(response);
    }
}
