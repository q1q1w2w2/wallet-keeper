package com.project.wallet_keeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budget")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @Column(name = "amount")
    private int amount;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Budget(int amount, int year, int month, User user) {
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.user = user;
    }

    public Budget update(int amount) {
        this.amount = amount;
        return this;
    }
}
