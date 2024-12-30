package com.project.wallet_keeper.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;
}
