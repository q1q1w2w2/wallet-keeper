package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.RegularExpense;
import com.project.wallet_keeper.domain.RegularIncome;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.service.TransactionScheduler;
import com.project.wallet_keeper.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "RegularTransactionController", description = "정기 거래 컨트롤러 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction/scheduler")
public class RegularTransactionController {

    private final TransactionScheduler transactionScheduler;
    private final UserService userService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularIncome(@Valid @RequestBody TransactionDto incomeDto) {
        User user = getCurrentUser();
        RegularIncome regularIncome = transactionScheduler.saveRegularIncome(user, incomeDto);

        return createResponse(CREATED, new RegularTransactionResponseDto(regularIncome));
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularExpense(@Valid @RequestBody TransactionDto incomeDto) {
        User user = getCurrentUser();
        RegularExpense regularExpense = transactionScheduler.saveRegularExpense(user, incomeDto);

        return createResponse(CREATED, new RegularTransactionResponseDto(regularExpense));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegularTransactionResponseDto>>> getRegularTransactions() {
        User user = getCurrentUser();
        List<RegularTransactionResponseDto> regularTransactions = transactionScheduler.getRegularTransactions(user);

        return createResponse(OK, regularTransactions);
    }

    @PatchMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<Object>> updateRegularIncome(@PathVariable Long incomeId, @Valid @RequestBody TransactionDto transactionDto) {
        User user = getCurrentUser();
        RegularIncome regularIncome = transactionScheduler.updateRegularIncome(user, incomeId, transactionDto);

        return createResponse(OK, new RegularTransactionResponseDto(regularIncome));
    }

    @PatchMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<Object>> updateRegularExpense(@PathVariable Long expenseId, @Valid @RequestBody TransactionDto transactionDto) {
        User user = getCurrentUser();
        RegularExpense regularExpense = transactionScheduler.updateRegularExpense(user, expenseId, transactionDto);

        return createResponse(OK, new RegularTransactionResponseDto(regularExpense));
    }

    @DeleteMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<Object>> deleteRegularIncome(@PathVariable Long incomeId) {
        User user = getCurrentUser();
        transactionScheduler.deleteRegularIncome(user, incomeId);

        return createResponse(NO_CONTENT);
    }

    @DeleteMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<Object>> deleteRegularExpense(@PathVariable Long expenseId) {
        User user = getCurrentUser();
        transactionScheduler.deleteRegularExpense(user, expenseId);

        return createResponse(NO_CONTENT);
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
