package com.project.wallet_keeper.dto.budget;

import com.project.wallet_keeper.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BudgetResultDto {
    private Budget budget;
    private Boolean isNew;
}
