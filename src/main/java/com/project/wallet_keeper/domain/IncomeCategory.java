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

    @Column(name = "income_category_name", nullable = false)
    private String categoryName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    public IncomeCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
