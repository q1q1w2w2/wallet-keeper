package com.project.wallet_keeper.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePasswordDto {

    private String oldPassword;

    @Size(min = 3, max = 30, message = "비밀번호는 3~20자 사이어야 합니다.")
    private String newPassword;
}
