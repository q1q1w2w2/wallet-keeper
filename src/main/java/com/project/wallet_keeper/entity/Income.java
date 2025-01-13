package com.project.wallet_keeper.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "income")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Income extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_category_id")
    private IncomeCategory incomeCategory;

    @Column(name = "income_at")
    private LocalDateTime incomeAt;

    @Builder
    public Income(String detail, Integer amount, String description, User user, LocalDateTime incomeAt, IncomeCategory incomeCategory) {
        super(detail, amount, description, user);
        this.incomeCategory = incomeCategory;
        this.incomeAt = incomeAt;
    }

    public Income update(String detail, Integer amount, String description, LocalDateTime incomeAt, IncomeCategory incomeCategory) {
        super.update(detail, amount, description);
        this.incomeCategory = incomeCategory;
        this.incomeAt = incomeAt;
        return this;
    }
}
