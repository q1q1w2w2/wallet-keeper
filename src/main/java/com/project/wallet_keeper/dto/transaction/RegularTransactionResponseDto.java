package com.project.wallet_keeper.dto.transaction;

import com.project.wallet_keeper.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RegularTransactionResponseDto {

    private static final String INCOME = "INCOME";
    private static final String EXPENSE = "EXPENSE";

    private Long transactionId;
    private String detail;
    private int amount;
    private String description;
    private String transactionCategory;
    private LocalDateTime transactionAt;
    private String transactionType;

    public RegularTransactionResponseDto(Transaction transaction) {
        this.transactionId = transaction instanceof RegularIncome ?
                ((RegularIncome) transaction).getId() :
                ((RegularExpense) transaction).getId();
        this.detail = transaction.getDetail();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transactionCategory = transaction instanceof RegularIncome ?
                ((RegularIncome) transaction).getIncomeCategory().getCategoryName() :
                ((RegularExpense) transaction).getExpenseCategory().getCategoryName();
        this.transactionAt = transaction instanceof RegularIncome ?
                ((RegularIncome) transaction).getIncomeAt() :
                ((RegularExpense) transaction).getExpenseAt();
        this.transactionType = transaction instanceof RegularIncome ? INCOME : EXPENSE;
    }

}
