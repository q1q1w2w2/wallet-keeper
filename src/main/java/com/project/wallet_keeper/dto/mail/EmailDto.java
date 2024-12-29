package com.project.wallet_keeper.dto.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

    @Email
    @NotNull(message = "이메일은 비어있을 수 없습니다.")
    private String email;
}
