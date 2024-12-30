package com.project.wallet_keeper.dto.mail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeDto {

    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotEmpty(message = "인증 번호는 비어있을 수 없습니다.")
    private String code;
}
