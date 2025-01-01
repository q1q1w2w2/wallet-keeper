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

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponseDto>> signUp(@Valid @RequestBody SignupDto signupDto) {
        User user = userService.signUp(signupDto);

        SignupResponseDto data = new SignupResponseDto(user);
        return createResponse(HttpStatus.CREATED, "회원가입이 완료되었습니다.", data);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser() {
        User user = userService.getCurrentUser();

        UserResponseDto data = new UserResponseDto(user);
        return createResponse(HttpStatus.OK, data);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateCurrentUser(@Valid @RequestBody UserProfileUpdateDto updateDto) {
        User user = userService.getCurrentUser();
        User updateUser = userService.updateUser(user, updateDto);

        UserResponseDto data = new UserResponseDto(updateUser);
        return createResponse(HttpStatus.OK, "사용자 정보가 수정되었습니다.", data);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteCurrentUser() {
        User user = userService.getCurrentUser();
        userService.deleteUser(user);

        return createResponse(HttpStatus.OK, "회원 탈퇴가 완료되었습니다.");
    }

    @PatchMapping("/me/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        User user = userService.getCurrentUser();
        userService.updatePassword(user, updatePasswordDto);

        return createResponse(HttpStatus.OK, "비밀번호가 변경되었습니다.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);

        return createResponse(HttpStatus.OK, "비밀번호가 변경되었습니다.");
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.success(status, message, data);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message, null);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, "Success", data);
        return ResponseEntity.status(status).body(response);
    }
}
