package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CategoryResponseDto;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.util.common.ApiResponse;
import com.project.wallet_keeper.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.wallet_keeper.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

@Tag(name = "CategoryController", description = "카테고리 컨트롤러 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<IncomeCategory>> createIncomeCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        IncomeCategory category = categoryService.createIncomeCategory(categoryDto);
        return createResponse(CREATED, category);
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createExpenseCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        ExpenseCategory category = categoryService.createExpenseCategory(categoryDto);
        return createResponse(CREATED, category);
    }

    @GetMapping("/income")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getIncomeCategoryList() {
        List<CategoryResponseDto> incomeCategories = categoryService.getIncomeCategories();
        return createResponse(OK, incomeCategories);
    }

    @GetMapping("/expense")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getExpenseCategoryList() {
        List<CategoryResponseDto> expenseCategories = categoryService.getExpenseCategories();
        return createResponse(OK, expenseCategories);
    }

    @PatchMapping("/income/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteIncomeCategory(@PathVariable Long categoryId) {
        categoryService.deleteIncomeCategory(categoryId);
        return createResponse(NO_CONTENT);
    }

    @PatchMapping("/expense/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteExpenseCategory(@PathVariable Long categoryId) {
        categoryService.deleteExpenseCategory(categoryId);
        return createResponse(NO_CONTENT);
    }
}
