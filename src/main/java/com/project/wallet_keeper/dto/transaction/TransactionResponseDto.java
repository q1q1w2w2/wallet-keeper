package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.entity.Expense;
import com.project.wallet_keeper.entity.Income;
import com.project.wallet_keeper.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponseDto implements Serializable {

    private static final String INCOME = "INCOME";
    private static final String EXPENSE = "EXPENSE";

    private Long transactionId;
    private String detail;
    private int amount;
    private String description;
    private String transactionCategory;
    private LocalDateTime transactionAt;
    private String transactionType;

    public TransactionResponseDto(Transaction transaction) {
        this.transactionId = transaction instanceof Income ?
                ((Income) transaction).getId() :
                ((Expense) transaction).getId();
        this.detail = transaction.getDetail();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transactionCategory = transaction instanceof Income ?
                ((Income) transaction).getIncomeCategory().getCategoryName() :
                ((Expense) transaction).getExpenseCategory().getCategoryName();
        this.transactionAt = transaction instanceof Income ?
                ((Income) transaction).getIncomeAt() :
                ((Expense) transaction).getExpenseAt();
        this.transactionType = transaction instanceof Income ? INCOME : EXPENSE;
    }
}
