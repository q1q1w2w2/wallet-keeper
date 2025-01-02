package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.SaveTransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.ExpenseRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import com.project.wallet_keeper.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    public Income saveIncome(User user, SaveTransactionDto incomeDto) {
        Long categoryId = incomeDto.getTransactionCategoryId();
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        Income income = Income.builder()
                .user(user)
                .detail(incomeDto.getDetail())
                .amount(incomeDto.getAmount())
                .description(incomeDto.getDescription() != null ? incomeDto.getDescription() : "")
                .incomeCategory(category)
                .incomeAt(incomeDto.getTransactionAt())
                .build();
        return incomeRepository.save(income);
    }

    @Transactional
    public Expense saveExpense(User user, SaveTransactionDto expenseDto) {
        Long categoryId = expenseDto.getTransactionCategoryId();
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        Expense expense = Expense.builder()
                .user(user)
                .detail(expenseDto.getDetail())
                .amount(expenseDto.getAmount())
                .description(expenseDto.getDescription() != null ? expenseDto.getDescription() : "")
                .expenseCategory(category)
                .expenseAt(expenseDto.getTransactionAt())
                .build();
        return expenseRepository.save(expense);
    }

    public List<TransactionResponseDto> getTransactionList(User user) {
        List<Income> incomeList = incomeRepository.findByUser(user);
        List<Expense> expenseList = expenseRepository.findByUser(user);

        ArrayList<Transaction> transactionList = new ArrayList<>();
        transactionList.addAll(incomeList);
        transactionList.addAll(expenseList);

        return sortAndConvertToDto(transactionList);
    }

    public List<TransactionResponseDto> getExpenseList(User user) {
        List<Expense> expenseList = expenseRepository.findByUser(user);
        return sortAndConvertToDto(expenseList);
    }

    private List<TransactionResponseDto> sortAndConvertToDto(List<? extends Transaction> transactionList) {
        List<TransactionResponseDto> responseList = convertToDto(transactionList);

        Collections.sort(responseList, new Comparator<TransactionResponseDto>() {
            @Override
            public int compare(TransactionResponseDto t1, TransactionResponseDto t2) {
                return t2.getTransactionAt().compareTo(t1.getTransactionAt());
            }
        });

        return responseList;
    }

    private List<TransactionResponseDto> convertToDto(List<? extends Transaction> transactionList) {
        ArrayList<TransactionResponseDto> responseList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            responseList.add(new TransactionResponseDto(transaction));
        }
        return responseList;
    }

}
