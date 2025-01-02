package com.project.wallet_keeper.dto.transaction;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SaveIncomeDto {

    private String detail;

    @NotNull(message = "금액은 비어있을 수 없습니다.")
    private Integer amount;

    private String description;

    @NotNull(message = "날짜는 비어있을 수 없습니다.")
    private LocalDateTime incomeAt;

    @NotNull(message = "카테고리 ID는 비어있을 수 없습니다.")
    private Long incomeCategoryId;
}
