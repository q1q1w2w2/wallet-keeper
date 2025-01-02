package com.project.wallet_keeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Transaction {

    @Column(name = "detail")
    private String detail;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transaction(String detail, Integer amount, String description, User user) {
        this.detail = detail;
        this.amount = amount;
        this.description = description;
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public void update(String detail, Integer amount, String description) {
        this.detail = detail;
        this.amount = amount;
        this.description = description;
    }

}
