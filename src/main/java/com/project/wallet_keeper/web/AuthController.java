package com.project.wallet_keeper.web;

import com.project.wallet_keeper.dto.auth.AccessTokenDto;
import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.RefreshTokenDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@Valid @RequestBody LoginDto loginDto) throws Exception {
        TokenDto tokens = authService.login(loginDto);

        ApiResponse<TokenDto> response = ApiResponse.success(HttpStatus.OK, "로그인 되었습니다.", tokens);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<TokenDto>> logout(@Valid @RequestBody TokenDto tokenDto) throws Exception {
        authService.logout(tokenDto);

        ApiResponse<TokenDto> response = ApiResponse.success(HttpStatus.OK, "로그아웃 되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<AccessTokenDto>> refreshToken(@Valid @RequestBody RefreshTokenDto tokenDto) throws Exception {
        String newAccessToken = authService.generateNewAccessTokens(tokenDto);

        ApiResponse<AccessTokenDto> response = ApiResponse.success(HttpStatus.OK, "토큰이 재발급 되었습니다.", new AccessTokenDto(newAccessToken));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
