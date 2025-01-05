package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.Budget;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.budget.BudgetDto;
import com.project.wallet_keeper.dto.budget.BudgetResponseDto;
import com.project.wallet_keeper.dto.budget.BudgetResultDto;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.service.BudgetService;
import com.project.wallet_keeper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @PutMapping
    public ResponseEntity<ApiResponse<BudgetResponseDto>> saveBudget(@Valid @RequestBody BudgetDto budgetDto) {
        User user = getCurrentUser();
        BudgetResultDto budgetResultDto = budgetService.saveOrUpdate(user, budgetDto);
        Budget budget = budgetResultDto.getBudget();

        if (budgetResultDto.getIsNew()) {
            return createResponse(CREATED, new BudgetResponseDto(budget));
        }
        return createResponse(OK, new BudgetResponseDto(budget));
    }

    @GetMapping
    public ResponseEntity getBudget(@RequestParam int year, @RequestParam int month) {
        User user = getCurrentUser();
        Budget budget = budgetService.findBudgetByDate(user, year, month);

        return createResponse(OK, new BudgetResponseDto(budget));
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status) {
        ApiResponse<T> response = ApiResponse.success(status);
        return ResponseEntity.status(status).body(response);
    }
}
