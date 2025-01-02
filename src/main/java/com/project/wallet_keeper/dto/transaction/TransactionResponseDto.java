package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.domain.Expense;
import com.project.wallet_keeper.domain.Income;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponseDto {

    private String detail;
    private int amount;
    private String description;
    private LocalDateTime transactionAt;

    public TransactionResponseDto(Expense expense) {
        this.detail = expense.getDetail();
        this.amount = expense.getAmount();
        this.description = expense.getDescription();
        this.transactionAt = expense.getExpenseAt();
    }

    public TransactionResponseDto(Income income) {
        this.detail = income.getDetail();
        this.amount = income.getAmount();
        this.description = income.getDescription();
        this.transactionAt = income.getIncomeAt();
    }

}
