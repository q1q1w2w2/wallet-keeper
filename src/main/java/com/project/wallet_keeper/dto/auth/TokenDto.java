package com.project.wallet_keeper.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenDto {

    @NotEmpty(message = "accessToken은 비어있을 수 없습니다.")
    private String accessToken;

    @NotEmpty(message = "refreshToken은 비어있을 수 없습니다.")
    private String refreshToken;
}
