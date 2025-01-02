package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.Expense;
import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.SaveTransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.service.TransactionService;
import com.project.wallet_keeper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveIncome(@Valid @RequestBody SaveTransactionDto incomeDto) {
        User user = userService.getCurrentUser();
        Income income = transactionService.saveIncome(user, incomeDto);

        ApiResponse<TransactionResponseDto> response = ApiResponse.success(HttpStatus.CREATED, new TransactionResponseDto(income));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveExpense(@Valid @RequestBody SaveTransactionDto expenseDto) {
        User user = userService.getCurrentUser();
        Expense expense = transactionService.saveExpense(user, expenseDto);

        ApiResponse<TransactionResponseDto> response = ApiResponse.success(HttpStatus.CREATED, new TransactionResponseDto(expense));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
