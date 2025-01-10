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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/transaction/scheduler")
public class RegularTransactionController {

    private final TransactionScheduler transactionScheduler;
    private final UserService userService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularIncome(@Valid @RequestBody TransactionDto incomeDto) {
        User user = userService.getCurrentUser();
        RegularIncome regularIncome = transactionScheduler.saveRegularIncome(user, incomeDto);

        return createResponse(CREATED, new RegularTransactionResponseDto(regularIncome));
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<RegularTransactionResponseDto>> saveRegularExpense(@Valid @RequestBody TransactionDto incomeDto) {
        User user = userService.getCurrentUser();
        RegularExpense regularExpense = transactionScheduler.saveRegularExpense(user, incomeDto);

        return createResponse(CREATED, new RegularTransactionResponseDto(regularExpense));
    }

    @GetMapping("/income")
    public ResponseEntity<ApiResponse<List<RegularTransactionResponseDto>>> getRegularIncomes() {
        User user = userService.getCurrentUser();
        List<RegularTransactionResponseDto> regularIncomes = transactionScheduler.getRegularIncomes(user);

        return createResponse(OK, regularIncomes);
    }

    @GetMapping("/expense")
    public ResponseEntity<ApiResponse<List<RegularTransactionResponseDto>>> getRegularExpenses() {
        User user = userService.getCurrentUser();
        List<RegularTransactionResponseDto> regularExpenses = transactionScheduler.getRegularExpenses(user);

        return createResponse(OK, regularExpenses);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }


}
