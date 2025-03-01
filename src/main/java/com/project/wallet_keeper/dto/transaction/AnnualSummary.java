package com.project.wallet_keeper.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnualSummary implements Serializable {

    private int year;
    private long totalIncome;
    private long totalExpense;
    private long total;
    private Map<String, MonthlySummary> monthly;
}
