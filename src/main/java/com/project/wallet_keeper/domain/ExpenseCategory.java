package com.project.wallet_keeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expense_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_category_id")
    private Long id;

    @Column(name = "expense_category_name")
    private String categoryName;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void active() {
        this.isDeleted = false;
    }
}
