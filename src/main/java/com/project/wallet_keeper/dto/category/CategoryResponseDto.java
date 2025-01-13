package com.project.wallet_keeper.dto.category;

import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponseDto {

    private Long categoryId;
    private String categoryName;

    public CategoryResponseDto(IncomeCategory incomeCategory) {
        this.categoryId = incomeCategory.getId();
        this.categoryName = incomeCategory.getCategoryName();
    }

    public CategoryResponseDto(ExpenseCategory expenseCategory) {
        this.categoryId = expenseCategory.getId();
        this.categoryName = expenseCategory.getCategoryName();
    }
}
