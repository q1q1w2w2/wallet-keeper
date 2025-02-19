package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.RegularExpense;
import com.project.wallet_keeper.entity.RegularIncome;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.service.TransactionScheduler;
import com.project.wallet_keeper.service.UserService;
import com.project.wallet_keeper.util.auth.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularIncome(@Valid @RequestBody TransactionDto incomeDto, @LoginUser User user) {
        RegularIncome regularIncome = transactionScheduler.saveRegularIncome(user, incomeDto);
        return createResponse(CREATED, new RegularTransactionResponseDto(regularIncome));
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularExpense(@Valid @RequestBody TransactionDto incomeDto, @LoginUser User user) {
        RegularExpense regularExpense = transactionScheduler.saveRegularExpense(user, incomeDto);
        return createResponse(CREATED, new RegularTransactionResponseDto(regularExpense));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegularTransactionResponseDto>>> getRegularTransactions(@LoginUser User user) {
        List<RegularTransactionResponseDto> regularTransactions = transactionScheduler.getRegularTransactions(user);
        return createResponse(OK, regularTransactions);
    }

    @PatchMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<Object>> updateRegularIncome(@PathVariable Long incomeId, @Valid @RequestBody TransactionDto transactionDto, @LoginUser User user) {
        RegularIncome regularIncome = transactionScheduler.updateRegularIncome(user, incomeId, transactionDto);
        return createResponse(OK, new RegularTransactionResponseDto(regularIncome));
    }

    @PatchMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<Object>> updateRegularExpense(@PathVariable Long expenseId, @Valid @RequestBody TransactionDto transactionDto, @LoginUser User user) {
        RegularExpense regularExpense = transactionScheduler.updateRegularExpense(user, expenseId, transactionDto);
        return createResponse(OK, new RegularTransactionResponseDto(regularExpense));
    }

    @DeleteMapping("/income/{incomeId}")
    public ResponseEntity<ApiResponse<Object>> deleteRegularIncome(@PathVariable Long incomeId, @LoginUser User user) {
        transactionScheduler.deleteRegularIncome(user, incomeId);
        return createResponse(NO_CONTENT);
    }

    @DeleteMapping("/expense/{expenseId}")
    public ResponseEntity<ApiResponse<Object>> deleteRegularExpense(@PathVariable Long expenseId, @LoginUser User user) {
        transactionScheduler.deleteRegularExpense(user, expenseId);
        return createResponse(NO_CONTENT);
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
