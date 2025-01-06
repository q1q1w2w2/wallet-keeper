package com.project.wallet_keeper.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnualSummary {

    private int year;
    private int totalIncome;
    private int totalExpense;
    private int total;
    private Map<String, MonthlySummary> monthly;
}
