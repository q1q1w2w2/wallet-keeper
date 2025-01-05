package com.project.wallet_keeper.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSummary {

    private Long categoryId;
    private String categoryName;
    private int amount;
}
