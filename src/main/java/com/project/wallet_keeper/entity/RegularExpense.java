package com.project.wallet_keeper.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "regular_expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegularExpense extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regular_expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory expenseCategory;

    @Column(name = "expense_at")
    private LocalDateTime expenseAt;

    @Builder
    public RegularExpense(String detail, Integer amount, String description, User user, LocalDateTime expenseAt, ExpenseCategory expenseCategory) {
        super(detail, amount, description, user);
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
    }

    public RegularExpense update(String detail, Integer amount, String description, LocalDateTime expenseAt, ExpenseCategory expenseCategory) {
        super.update(detail, amount, description);
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
        return this;
    }
}
