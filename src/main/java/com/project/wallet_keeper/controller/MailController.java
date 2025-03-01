package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.dto.mail.EmailDto;
import com.project.wallet_keeper.dto.mail.VerifyCodeDto;
import com.project.wallet_keeper.util.common.ApiResponse;
import com.project.wallet_keeper.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.wallet_keeper.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

@Tag(name = "MailController", description = "메일 컨트롤러 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<Object>> sendCode(@Valid @RequestBody EmailDto emailDto) throws MessagingException {
        mailService.sendMailForSignup(emailDto.getEmail());
        return createResponse(OK, "인증 메일이 발송되었습니다.");
    }

    @PostMapping("/verification/code")
    public ResponseEntity<ApiResponse<Object>> verifyCode(@Valid @RequestBody VerifyCodeDto verifyCodeDto) {
        mailService.verifyCode(verifyCodeDto.getEmail(), verifyCodeDto.getCode());
        return createResponse(OK, "인증이 완료되었습니다.");
    }
}
