package com.project.wallet_keeper.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OAuthDto {
    private String email;
    private String name;
    private String provider;
    private String isExist;
}
