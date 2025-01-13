package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.ExpenseCategory;
import com.project.wallet_keeper.entity.IncomeCategory;
import com.project.wallet_keeper.dto.category.CategoryResponseDto;
import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

        ApiResponse<IncomeCategory> response = ApiResponse.success(CREATED, category);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createExpenseCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        ExpenseCategory category = categoryService.createExpenseCategory(categoryDto);

        ApiResponse<ExpenseCategory> response = ApiResponse.success(CREATED, category);
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping("/income")
    public ResponseEntity<ApiResponse<ArrayList<CategoryResponseDto>>> getIncomeCategoryList() {
        List<IncomeCategory> incomeCategories = categoryService.getIncomeCategories();

        ArrayList<CategoryResponseDto> categoryList = new ArrayList<>();
        for (IncomeCategory incomeCategory : incomeCategories) {
            categoryList.add(new CategoryResponseDto(incomeCategory));
        }

        ApiResponse<ArrayList<CategoryResponseDto>> response = ApiResponse.success(OK, categoryList);
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/expense")
    public ResponseEntity<ApiResponse<ArrayList<CategoryResponseDto>>> getExpenseCategoryList() {
        List<ExpenseCategory> expenseCategories = categoryService.getExpenseCategories();

        ArrayList<CategoryResponseDto> categoryList = new ArrayList<>();
        for (ExpenseCategory expenseCategory : expenseCategories) {
            categoryList.add(new CategoryResponseDto(expenseCategory));
        }

        ApiResponse<ArrayList<CategoryResponseDto>> response = ApiResponse.success(OK, categoryList);
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/income/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteIncomeCategory(@PathVariable Long categoryId) {
        categoryService.deleteIncomeCategory(categoryId);

        ApiResponse<Object> response = ApiResponse.success(NO_CONTENT);
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/expense/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteExpenseCategory(@PathVariable Long categoryId) {
        categoryService.deleteExpenseCategory(categoryId);

        ApiResponse<Object> response = ApiResponse.success(NO_CONTENT);
        return ResponseEntity.status(OK).body(response);
    }
}
