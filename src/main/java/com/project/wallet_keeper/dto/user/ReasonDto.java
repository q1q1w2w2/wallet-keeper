package com.project.wallet_keeper.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReasonDto {

    @NotEmpty(message = "탈퇴 사유를 적어주세요.")
    private String reason;
}
