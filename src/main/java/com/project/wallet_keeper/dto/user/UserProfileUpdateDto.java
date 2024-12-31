package com.project.wallet_keeper.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileUpdateDto {

    @NotEmpty(message = "닉네임은 비어있을 수 없습니다.")
    @Size(min = 2, max = 30, message = "닉네임은 2~30자 사이어야 합니다.")
    private String nickname;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birth;
}
