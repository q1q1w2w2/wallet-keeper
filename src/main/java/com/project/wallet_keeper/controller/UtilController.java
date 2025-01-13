package com.project.wallet_keeper.controller;

import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.service.TransactionService;
import com.project.wallet_keeper.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UtilController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/api/auth/redirect")
    public String redirectForOAuth(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String provider,
            @RequestParam boolean isExist,
            Model model
    ) {
        log.info("email: {}, name: {}, provider: {}, isExist: {}", email, name, provider, isExist);

        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("provider", provider);
        model.addAttribute("isExist", isExist);
        return "auth/redirect";
    }


    @GetMapping("/api/transaction/excel")
    public void downloadTransaction(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            HttpServletResponse response) throws IOException {
        User user = userService.getCurrentUser();
        transactionService.generateExcelForTransaction(user, startDate, endDate, response);
    }
}
