package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.domain.Expense;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseResponseDto {

    private String detail;
    private int amount;
    private String description;
    private LocalDateTime expenseAt;

    public ExpenseResponseDto(Expense expense) {
        this.detail = expense.getDetail();
        this.amount = expense.getAmount();
        this.description = expense.getDescription();
        this.expenseAt = expense.getExpenseAt();
    }
}
