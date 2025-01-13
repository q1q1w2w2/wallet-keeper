package com.project.wallet_keeper.dto.user;

import com.project.wallet_keeper.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private LocalDate birth;
    private LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.birth = user.getBirth();
        this.createdAt = user.getCreatedAt();
    }
}
