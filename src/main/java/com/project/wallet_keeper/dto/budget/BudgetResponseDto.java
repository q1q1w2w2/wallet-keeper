package com.project.wallet_keeper.dto.budget;

import com.project.wallet_keeper.domain.Budget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BudgetResponseDto {

    private int amount;
    private int year;
    private int month;

    public BudgetResponseDto(Budget budget) {
        this.amount = budget.getAmount();
        this.year = budget.getYear();
        this.month = budget.getMonth();
    }
}
