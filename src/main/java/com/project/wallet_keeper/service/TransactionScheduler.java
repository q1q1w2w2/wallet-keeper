package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionScheduler {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final RegularIncomeRepository regularIncomeRepository;
    private final RegularExpenseRepository regularExpenseRepository;

    @Transactional
    public RegularIncome saveRegularIncome(User user, TransactionDto transactionDto) {
        Long categoryId = transactionDto.getTransactionCategoryId();
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        RegularIncome regularIncome = RegularIncome.builder()
                .user(user)
                .detail(transactionDto.getDetail())
                .amount(transactionDto.getAmount())
                .description(transactionDto.getDescription() != null ? transactionDto.getDescription() : "")
                .incomeCategory(category)
                .incomeAt(transactionDto.getTransactionAt())
                .build();
        return regularIncomeRepository.save(regularIncome);
    }

    @Transactional
    public RegularExpense saveRegularExpense(User user, TransactionDto transactionDto) {
        Long categoryId = transactionDto.getTransactionCategoryId();
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        RegularExpense regularExpense = RegularExpense.builder()
                .user(user)
                .detail(transactionDto.getDetail())
                .amount(transactionDto.getAmount())
                .description(transactionDto.getDescription() != null ? transactionDto.getDescription() : "")
                .expenseCategory(category)
                .expenseAt(transactionDto.getTransactionAt())
                .build();
        return regularExpenseRepository.save(regularExpense);
    }

    public List<RegularTransactionResponseDto> getRegularIncomes(User user) {
        List<RegularIncome> list = regularIncomeRepository.findAllByUser(user);
        return convertToDto(list);
    }

    public List<RegularTransactionResponseDto> getRegularExpenses(User user) {
        List<RegularExpense> list = regularExpenseRepository.findAllByUser(user);
        return convertToDto(list);
    }

    private List<RegularTransactionResponseDto> convertToDto(List<? extends Transaction> transactionList) {
        return transactionList.stream()
                .map(RegularTransactionResponseDto::new)
                .sorted(Comparator.comparing(RegularTransactionResponseDto::getTransactionAt).reversed())
                .toList();
    }

    @Transactional
    @Scheduled(cron = "0 0 4 1 * ?") // 매달 1일 새벽 4시
    public void saveRegularIncome() {
        try {
            List<RegularIncome> regularIncomes = regularIncomeRepository.findAll();
            for (RegularIncome regularIncome : regularIncomes) {
                Income income = Income.builder()
                        .user(regularIncome.getUser())
                        .detail(regularIncome.getDetail())
                        .amount(regularIncome.getAmount())
                        .description(regularIncome.getDescription() != null ? regularIncome.getDescription() : "")
                        .incomeCategory(regularIncome.getIncomeCategory())
                        .incomeAt(regularIncome.getIncomeAt())
                        .build();

                incomeRepository.save(income);
            }
        } catch (Exception e) {
            log.error("반복적인 수입 항목을 저장하던 중 오류가 발생했습니다: {}", e.getMessage());
        }

    }

    @Transactional
    @Scheduled(cron = "0 0 4 1 * ?")
    public void saveRegularExpense() {
        try {
            List<RegularExpense> regularExpenses = regularExpenseRepository.findAll();
            for (RegularExpense regularExpense : regularExpenses) {
                Expense expense = Expense.builder()
                        .user(regularExpense.getUser())
                        .detail(regularExpense.getDetail())
                        .amount(regularExpense.getAmount())
                        .description(regularExpense.getDescription() != null ? regularExpense.getDescription() : "")
                        .expenseCategory(regularExpense.getExpenseCategory())
                        .expenseAt(regularExpense.getExpenseAt())
                        .build();

                expenseRepository.save(expense);
            }
        } catch (Exception e) {
            log.error("반복적인 지출 항목을 저장하던 중 오류가 발생했습니다: {}", e.getMessage());
        }
    }
}
