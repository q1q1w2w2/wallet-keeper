package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.transaction.TransactionResponseDto;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.repository.ExpenseCategoryRepository;
import com.project.wallet_keeper.repository.ExpenseRepository;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import com.project.wallet_keeper.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional
    public Income saveIncome(User user, TransactionDto incomeDto) {
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
    public Expense saveExpense(User user, TransactionDto expenseDto) {
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

    public Income getIncome(Long incomeId) {
        return incomeRepository.findById(incomeId)
                .orElseThrow(TransactionNotFoundException::new);
    }

    public Expense getExpense(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(TransactionNotFoundException::new);
    }

    @Transactional
    public Income updateIncome(Long incomeId, TransactionDto incomeDto, User user) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(TransactionNotFoundException::new);

        checkTransactionOwnership(income, user);

        IncomeCategory category = incomeCategoryRepository.findById(incomeDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return income.update(incomeDto.getDetail(), incomeDto.getAmount(), incomeDto.getDescription(), incomeDto.getTransactionAt(), category);
    }

    @Transactional
    public Expense updateExpense(Long expenseId, TransactionDto expenseDto, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(TransactionNotFoundException::new);

        checkTransactionOwnership(expense, user);

        ExpenseCategory category = expenseCategoryRepository.findById(expenseDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return expense.update(expenseDto.getDetail(), expenseDto.getAmount(), expenseDto.getDescription(), expenseDto.getTransactionAt(), category);
    }

    @Transactional
    public void deleteIncome(Long incomeId, User user) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(TransactionNotFoundException::new);
        checkTransactionOwnership(income, user);

        incomeRepository.delete(income);
    }

    @Transactional
    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(TransactionNotFoundException::new);
        checkTransactionOwnership(expense, user);

        expenseRepository.delete(expense);
    }

    private void checkTransactionOwnership(Transaction transaction, User user) {
        if (!transaction.getUser().equals(user)) {
            throw new InvalidTransactionOwnerException();
        }
    }
}
