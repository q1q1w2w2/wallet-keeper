package com.project.wallet_keeper.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SignupDto {

    @Email
    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 비어있을 수 없습니다.")
    private String password;

    @NotEmpty(message = "닉네임은 비어있을 수 없습니다.")
    private String nickname;

    @NotNull(message = "생년월일은 비어있을 수 없습니다.")
    private LocalDate birth;
}
