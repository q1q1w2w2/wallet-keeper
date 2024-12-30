package com.project.wallet_keeper.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;
}
