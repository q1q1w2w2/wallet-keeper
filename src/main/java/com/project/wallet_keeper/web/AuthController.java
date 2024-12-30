package com.project.wallet_keeper.web;

import com.project.wallet_keeper.dto.auth.LoginDto;
import com.project.wallet_keeper.dto.auth.TokenDto;
import com.project.wallet_keeper.dto.response.ApiResponse;
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
    public ResponseEntity login(@Valid @RequestBody LoginDto dto) throws Exception {
        TokenDto tokens = authService.login(dto);

        ApiResponse<TokenDto> response = ApiResponse.success(HttpStatus.OK, "로그인 되었습니다.", tokens);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
