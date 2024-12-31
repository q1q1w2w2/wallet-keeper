package com.project.wallet_keeper.web;

import com.project.wallet_keeper.dto.mail.EmailDto;
import com.project.wallet_keeper.dto.mail.VerifyCodeDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.MailService;
import jakarta.mail.MessagingException;
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
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<Object>> sendCode(@Valid @RequestBody EmailDto emailDto) throws MessagingException {
        mailService.sendMailForSignup(emailDto.getEmail());

        ApiResponse<Object> response = ApiResponse.success(HttpStatus.OK, "인증 메일이 발송되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verification/code")
    public ResponseEntity<ApiResponse<Object>> verifyCode(@Valid @RequestBody VerifyCodeDto verifyCodeDto) {
        mailService.verifyCode(verifyCodeDto.getEmail(), verifyCodeDto.getCode());

        ApiResponse<Object> response = ApiResponse.success(HttpStatus.OK, "인증이 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
