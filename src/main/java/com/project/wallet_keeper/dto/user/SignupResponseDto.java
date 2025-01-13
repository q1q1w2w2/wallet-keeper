package com.project.wallet_keeper.dto.user;

import com.project.wallet_keeper.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignupResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public SignupResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt();
    }
}
