package com.project.wallet_keeper.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetReport {

    private int budget;
    private int totalAmount;
    private int remainAmount;
    private int percent;
}
