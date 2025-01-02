package com.project.wallet_keeper.domain;

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
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
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
    @JoinColumn(name = "income_category_id")
    private IncomeCategory incomeCategory;

    @Column(name = "income_at")
    private LocalDateTime incomeAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Income(String detail, int amount, String description, User user, LocalDateTime incomeAt, IncomeCategory incomeCategory) {
        this.detail = detail;
        this.amount = amount;
        this.description = description;
        this.user = user;
        this.incomeCategory = incomeCategory;
        this.incomeAt = incomeAt;
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
