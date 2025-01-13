package com.project.wallet_keeper.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory expenseCategory;

    @Column(name = "expense_at")
    private LocalDateTime expenseAt;

    @Builder
    public Expense(String detail, Integer amount, String description, User user, ExpenseCategory expenseCategory, LocalDateTime expenseAt) {
        super(detail, amount, description, user);
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
    }

    public Expense update(String detail, Integer amount, String description, LocalDateTime expenseAt, ExpenseCategory expenseCategory) {
        super.update(detail, amount, description);
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
        return this;
    }

}
