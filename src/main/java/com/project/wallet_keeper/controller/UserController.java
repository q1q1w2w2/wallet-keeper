package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.user.*;
import com.project.wallet_keeper.util.common.ApiResponse;
import com.project.wallet_keeper.service.UserService;
import com.project.wallet_keeper.util.common.ApiResponseUtil;
import com.project.wallet_keeper.util.common.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.wallet_keeper.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

@Tag(name = "UserController", description = "사용자 컨트롤러 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponseDto>> signUp(@Valid @RequestBody SignupDto signupDto) {
        User user = userService.signUp(signupDto);
        return createResponse(CREATED, "회원가입이 완료되었습니다.", new SignupResponseDto(user));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(@LoginUser User user) {
        return createResponse(OK, new UserResponseDto(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateCurrentUser(@Valid @RequestBody UserProfileUpdateDto updateDto, @LoginUser User user) {
        User updateUser = userService.updateUser(user, updateDto);
        return createResponse(OK, "사용자 정보가 수정되었습니다.", new UserResponseDto(updateUser));
    }

    @PatchMapping("/me/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> deleteCurrentUser(@Valid @RequestBody ReasonDto reasonDto, @LoginUser User user) {
        userService.deleteUser(user, reasonDto.getReason());
        return createResponse(OK, "회원 탈퇴가 완료되었습니다.");
    }

    @PatchMapping("/me/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto, @LoginUser User user) {
        userService.updatePassword(user, updatePasswordDto);
        return createResponse(OK, "비밀번호가 변경되었습니다.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);
        return createResponse(OK, "비밀번호가 변경되었습니다.");
    }
}
