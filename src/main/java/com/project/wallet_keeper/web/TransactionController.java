package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.Expense;
import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.transaction.AnnualSummary;
import com.project.wallet_keeper.dto.transaction.ExpenseSummary;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.service.TransactionService;
import com.project.wallet_keeper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.*;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveIncome(@Valid @RequestBody TransactionDto incomeDto) {
        User user = getCurrentUser();
        Income income = transactionService.saveIncome(user, incomeDto);

        return createResponse(CREATED, new TransactionResponseDto(income));
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveExpense(@Valid @RequestBody TransactionDto expenseDto) {
        User user = getCurrentUser();
        Expense expense = transactionService.saveExpense(user, expenseDto);

        return createResponse(CREATED, new TransactionResponseDto(expense));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TransactionResponseDto>>> getTransactionList(
            @RequestParam("startDate") @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DATE) LocalDate endDate
            ) {
        User user = getCurrentUser();
        List<TransactionResponseDto> transactionList = transactionService.getTransactionList(user, startDate, endDate);

        return createResponse(OK, transactionList);
    }

    @GetMapping("/expense/list")
    public ResponseEntity<ApiResponse<List<TransactionResponseDto>>> getExpenseList(
            @RequestParam("startDate") @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DATE) LocalDate endDate
    ) {
        User user = getCurrentUser();
        List<TransactionResponseDto> expenseList = transactionService.getExpenseList(user, startDate, endDate);

        return createResponse(OK, expenseList);
    }

    @GetMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getIncome(@PathVariable Long incomeId) {
        Income income = transactionService.getIncome(incomeId);

        return createResponse(OK, new TransactionResponseDto(income));
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getExpense(@PathVariable Long expenseId) {
        Expense expense = transactionService.getExpense(expenseId);

        return createResponse(OK, new TransactionResponseDto(expense));
    }

    @PatchMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> updateIncome(@PathVariable Long incomeId, @Valid @RequestBody TransactionDto transactionDto) {
        User user = getCurrentUser();
        Income updateIncome = transactionService.updateIncome(incomeId, transactionDto, user);

        return createResponse(OK, new TransactionResponseDto(updateIncome));
    }

    @PatchMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> updateExpense(@PathVariable Long expenseId, @Valid @RequestBody TransactionDto transactionDto) {
        User user = getCurrentUser();
        Expense updateExpense = transactionService.updateExpense(expenseId, transactionDto, user);

        return createResponse(OK, new TransactionResponseDto(updateExpense));
    }

    @DeleteMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<Object>> deleteIncome(@PathVariable Long incomeId) {
        User user = getCurrentUser();
        transactionService.deleteIncome(incomeId, user);

        return createResponse(NO_CONTENT);
    }

    @DeleteMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<Object>> deleteExpense(@PathVariable Long expenseId) {
        User user = getCurrentUser();
        transactionService.deleteExpense(expenseId, user);

        return createResponse(NO_CONTENT);
    }

    @GetMapping("/expense/summary")
    public ResponseEntity<ApiResponse<List<ExpenseSummary>>> getExpenseSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DATE) LocalDate endDate
    ) {
        User user = getCurrentUser();
        List<ExpenseSummary> summaryList = transactionService.getExpenseSummary(user, startDate, endDate);

        return createResponse(OK, summaryList);
    }

    @GetMapping("/annual")
    public ResponseEntity<ApiResponse<AnnualSummary>> getAnnualSummary(@RequestParam int year) {
        User user = getCurrentUser();
        AnnualSummary annualSummary = transactionService.getAnnualSummary(user, year);

        return createResponse(OK, annualSummary);
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
