package com.project.wallet_keeper.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenseSummary {
    private Integer month;
    private Long totalExpense;
}
