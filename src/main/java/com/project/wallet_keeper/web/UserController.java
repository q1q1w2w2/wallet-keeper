package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.SignupResponseDto;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.dto.response.ApiResponse;
import com.project.wallet_keeper.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponseDto>> signUp(@Valid @RequestBody SignupDto signupDto) {
        User user = userService.signUp(signupDto);

        SignupResponseDto data = new SignupResponseDto(user.getNickname());
        ApiResponse<SignupResponseDto> response = ApiResponse.success(HttpStatus.OK, "회원가입이 완료되었습니다.", data);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
