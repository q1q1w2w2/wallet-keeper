package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.RegularExpense;
import com.project.wallet_keeper.domain.RegularIncome;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.service.TransactionScheduler;
import com.project.wallet_keeper.service.UserService;
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
@RequestMapping("/api/transaction/scheduler")
public class RegularTransactionController {

    private final TransactionScheduler transactionScheduler;
    private final UserService userService;

    @PostMapping("/income")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveRegularIncome(@Valid @RequestBody TransactionDto incomeDto) {
        User user = userService.getCurrentUser();
        RegularIncome regularIncome = transactionScheduler.saveRegularIncome(user, incomeDto);

        return createResponse(CREATED, new TransactionResponseDto(regularIncome));
    }

    @PostMapping("/expense")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> saveRegularExpense(@Valid @RequestBody TransactionDto incomeDto) {
        User user = userService.getCurrentUser();
        RegularExpense regularExpense = transactionScheduler.saveRegularExpense(user, incomeDto);

        return createResponse(CREATED, new TransactionResponseDto(regularExpense));
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }

}
