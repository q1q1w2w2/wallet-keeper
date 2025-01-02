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

    private static final String INCOME = "INCOME";
    private static final String EXPENSE = "EXPENSE";

    private String detail;
    private int amount;
    private String description;
    private LocalDateTime transactionAt;
    private String transactionType;

    public TransactionResponseDto(Income income) {
        this.detail = income.getDetail();
        this.amount = income.getAmount();
        this.description = income.getDescription();
        this.transactionAt = income.getIncomeAt();
        this.transactionType = INCOME;
    }

    public TransactionResponseDto(Expense expense) {
        this.detail = expense.getDetail();
        this.amount = expense.getAmount();
        this.description = expense.getDescription();
        this.transactionAt = expense.getExpenseAt();
        this.transactionType = EXPENSE;
    }
}
