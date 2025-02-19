package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.Budget;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetReport;
import com.project.wallet_keeper.dto.budget.BudgetResponseDto;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.BudgetService;
import com.project.wallet_keeper.service.UserService;
import com.project.wallet_keeper.util.auth.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "BudgetController", description = "예산 컨트롤러 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @PutMapping
    public ResponseEntity<ApiResponse<BudgetResponseDto>> saveBudget(@Valid @RequestBody BudgetDto budgetDto, @LoginUser User user) {
        BudgetResultDto budgetResultDto = budgetService.saveOrUpdate(user, budgetDto);
        Budget budget = budgetResultDto.getBudget();

        if (budgetResultDto.getIsNew()) {
            return createResponse(CREATED, new BudgetResponseDto(budget));
        }
        return createResponse(OK, new BudgetResponseDto(budget));
    }

    @GetMapping
    public ResponseEntity getBudget(@RequestParam int year, @RequestParam int month, @LoginUser User user) {
        Budget budget = budgetService.findBudgetByDate(user, year, month);
        return createResponse(OK, new BudgetResponseDto(budget));
    }

    @GetMapping("/report")
    public ResponseEntity report(@RequestParam int year, @RequestParam int month, @LoginUser User user) {
        BudgetReport report = budgetService.report(user, year, month);
        return createResponse(OK, report);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }
}