package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.*;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponseDto>> signUp(@Valid @RequestBody SignupDto signupDto) {
        User user = userService.signUp(signupDto);

        SignupResponseDto data = new SignupResponseDto(user);
        return createResponse(CREATED, "회원가입이 완료되었습니다.", data);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser() {
        User user = userService.getCurrentUser();

        UserResponseDto data = new UserResponseDto(user);
        return createResponse(OK, data);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateCurrentUser(@Valid @RequestBody UserProfileUpdateDto updateDto) {
        User user = userService.getCurrentUser();
        User updateUser = userService.updateUser(user, updateDto);

        UserResponseDto data = new UserResponseDto(updateUser);
        return createResponse(OK, "사용자 정보가 수정되었습니다.", data);
    }

    @PatchMapping("/me/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteCurrentUser(@Valid @RequestBody ReasonDto reasonDto) {
        User user = userService.getCurrentUser();
        userService.deleteUser(user, reasonDto.getReason());

        return createResponse(OK, "회원 탈퇴가 완료되었습니다.");
    }

    @PatchMapping("/me/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        User user = userService.getCurrentUser();
        userService.updatePassword(user, updatePasswordDto);

        return createResponse(OK, "비밀번호가 변경되었습니다.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);

        return createResponse(OK, "비밀번호가 변경되었습니다.");
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.success(status, message, data);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }
}
