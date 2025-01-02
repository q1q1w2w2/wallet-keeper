package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.domain.Expense;
import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.Transaction;
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

    public TransactionResponseDto(Transaction transaction) {
        this.detail = transaction.getDetail();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transactionAt = transaction instanceof Income ?
                ((Income) transaction).getIncomeAt() :
                ((Expense) transaction).getExpenseAt();
        this.transactionType = transaction instanceof Income ? INCOME : EXPENSE;
    }
}
