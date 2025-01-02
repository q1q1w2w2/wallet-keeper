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
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @Column(name = "detail")
    private String detail;

    @Column(name = "amount")
    private int amount;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory expenseCategory;

    @Column(name = "expense_at")
    private LocalDateTime expenseAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Expense(String detail, int amount, String description, User user, ExpenseCategory expenseCategory, LocalDateTime expenseAt) {
        this.detail = detail;
        this.amount = amount;
        this.description = description;
        this.user = user;
        this.expenseCategory = expenseCategory;
        this.expenseAt = expenseAt;
    }

    @PrePersist // 영속화 될 때의 시간을 설정하기 위해 사용
    public void prePersist() {
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}
