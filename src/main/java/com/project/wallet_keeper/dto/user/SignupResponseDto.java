package com.project.wallet_keeper.dto.user;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private String nickname;

    public SignupResponseDto(String nickname) {
        this.nickname = nickname;
    }
}
