package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.domain.Income;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class IncomeResponseDto {

    private String detail;
    private int amount;
    private String description;
    private LocalDateTime incomeAt;

    public IncomeResponseDto(Income income) {
        this.detail = income.getDetail();
        this.amount = income.getAmount();
        this.description = income.getDescription();
        this.incomeAt = income.getIncomeAt();
    }
}
