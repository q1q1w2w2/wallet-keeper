package com.project.wallet_keeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "income_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_category_id")
    private Long id;

    @Column(name = "income_category_name")
    private String categoryName;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public IncomeCategory(String categoryName, User user) {
        this.categoryName = categoryName;
        this.user = user;
    }

}
