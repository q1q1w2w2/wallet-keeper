package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.ExpenseCategory;
import com.project.wallet_keeper.domain.IncomeCategory;
import com.project.wallet_keeper.dto.CreateCategoryDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/transaction/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<IncomeCategory>> createIncomeCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        IncomeCategory category = categoryService.createIncomeCategory(categoryDto);

        ApiResponse<IncomeCategory> response = ApiResponse.success(CREATED, "수입 카테고리가 생성되었습니다.", category);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createExpenseCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        ExpenseCategory category = categoryService.createExpenseCategory(categoryDto);

        ApiResponse<ExpenseCategory> response = ApiResponse.success(CREATED, "지출 카테고리가 생성되었습니다.", category);
        return ResponseEntity.status(CREATED).body(response);
    }
}
