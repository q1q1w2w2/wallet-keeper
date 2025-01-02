package com.project.wallet_keeper.web;

import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.common.ApiResponse;
import com.project.wallet_keeper.dto.transaction.IncomeResponseDto;
import com.project.wallet_keeper.dto.transaction.SaveIncomeDto;
import com.project.wallet_keeper.service.IncomeService;
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
@RequestMapping("/api/transaction/income")
public class IncomeController {

    private final IncomeService incomeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponseDto>> saveIncome(@Valid @RequestBody SaveIncomeDto incomeDto) {
        User user = userService.getCurrentUser();
        Income income = incomeService.save(user, incomeDto);

        ApiResponse<IncomeResponseDto> response = ApiResponse.success(HttpStatus.CREATED, new IncomeResponseDto(income));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
