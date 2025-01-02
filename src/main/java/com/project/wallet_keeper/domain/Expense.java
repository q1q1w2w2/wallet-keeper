package com.project.wallet_keeper.domain;

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
    public Expense(String detail, int amount, String description, User user, ExpenseCategory expenseCategory, LocalDateTime expenseAt) {
        super(detail, amount, description, user);
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
    }

}
