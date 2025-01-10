package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.*;
import com.project.wallet_keeper.dto.transaction.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.addAll(incomeList);
        transactionList.addAll(expenseList);

        return sortAndConvertToDto(transactionList);
    }

    public List<TransactionResponseDto> getTransactionList(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Income> incomeList = incomeRepository.findByUserAndIncomeAtBetween(user, startDateTime, endDateTime);
        List<Expense> expenseList = expenseRepository.findByUserAndExpenseAtBetween(user, startDateTime, endDateTime);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.addAll(incomeList);
        transactionList.addAll(expenseList);

        return sortAndConvertToDto(transactionList);
    }

    public List<TransactionResponseDto> getExpenseList(User user) {
        List<Expense> expenseList = expenseRepository.findByUser(user);
        return sortAndConvertToDto(expenseList);
    }

    public List<TransactionResponseDto> getExpenseList(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Expense> expenseList = expenseRepository.findByUserAndExpenseAtBetween(user, startDateTime, endDateTime);
        return sortAndConvertToDto(expenseList);
    }

    private List<TransactionResponseDto> sortAndConvertToDto(List<? extends Transaction> transactionList) {
        return transactionList.stream()
                .map(TransactionResponseDto::new)
                .sorted(Comparator.comparing(TransactionResponseDto::getTransactionAt).reversed())
                .toList();
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
        Income income = getIncome(incomeId);

        checkTransactionOwnership(income, user);

        IncomeCategory category = incomeCategoryRepository.findById(incomeDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return income.update(incomeDto.getDetail(), incomeDto.getAmount(), incomeDto.getDescription(), incomeDto.getTransactionAt(), category);
    }

    @Transactional
    public Expense updateExpense(Long expenseId, TransactionDto expenseDto, User user) {
        Expense expense = getExpense(expenseId);

        checkTransactionOwnership(expense, user);

        ExpenseCategory category = expenseCategoryRepository.findById(expenseDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return expense.update(expenseDto.getDetail(), expenseDto.getAmount(), expenseDto.getDescription(), expenseDto.getTransactionAt(), category);
    }

    @Transactional
    public void deleteIncome(Long incomeId, User user) {
        Income income = getIncome(incomeId);
        checkTransactionOwnership(income, user);

        incomeRepository.delete(income);
    }

    @Transactional
    public void deleteExpense(Long expenseId, User user) {
        Expense expense = getExpense(expenseId);
        checkTransactionOwnership(expense, user);

        expenseRepository.delete(expense);
    }

    private void checkTransactionOwnership(Transaction transaction, User user) {
        if (!transaction.getUser().equals(user)) {
            throw new InvalidTransactionOwnerException();
        }
    }

    public List<ExpenseSummary> getExpenseSummary(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Expense> expenseList = expenseRepository.findByUserAndExpenseAtBetween(user, startDateTime, endDateTime);
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAllByIsDeletedFalse();

        Map<ExpenseCategory, Integer> categoryMap = new HashMap<>();

        for (ExpenseCategory expenseCategory : expenseCategories) {
            categoryMap.put(expenseCategory, 0);
        }

        for (Expense expense : expenseList) {
            ExpenseCategory category = expense.getExpenseCategory();
            Integer totalAmount = categoryMap.get(category);
            if (totalAmount != null) {
                categoryMap.put(category, totalAmount + expense.getAmount());
            }
        }

        int totalSpentAmount = categoryMap.values().stream().mapToInt(Integer::intValue).sum();
        ArrayList<ExpenseSummary> summaryList = new ArrayList<>();
        for (Map.Entry<ExpenseCategory, Integer> entry : categoryMap.entrySet()) {
            if (entry.getValue() > 0) {
                ExpenseSummary summary = new ExpenseSummary();
                summary.setCategoryId(entry.getKey().getId());
                summary.setCategoryName(entry.getKey().getCategoryName());
                summary.setAmount(entry.getValue());

                int percent = (int) ((entry.getValue() / (double) totalSpentAmount) * 100);
                summary.setPercent(percent);

                summaryList.add(summary);
            }
        }

        return summaryList;
    }

    public AnnualSummary getAnnualSummary(User user, int year) {
        LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, 12, 31, 23, 59);

        List<Income> incomeList = incomeRepository.findByUserAndIncomeAtBetween(user, startDateTime, endDateTime);
        List<Expense> expenseList = expenseRepository.findByUserAndExpenseAtBetween(user, startDateTime, endDateTime);

        String[] monthly = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        Map<String, MonthlySummary> monthlySummaryMap = new LinkedHashMap<>();

        int totalIncome = 0;
        int totalExpense = 0;

        for (String month : monthly) {
            monthlySummaryMap.put(month, new MonthlySummary());
        }

        for (Income income : incomeList) {
            String month = income.getIncomeAt().getMonth().toString();
            MonthlySummary summary = monthlySummaryMap.get(month);
            summary.setIncome(summary.getIncome() + income.getAmount());
        }

        for (Expense expense : expenseList) {
            String month = expense.getExpenseAt().getMonth().toString();
            MonthlySummary summary = monthlySummaryMap.get(month);
            summary.setExpense(summary.getExpense() + expense.getAmount());
        }

        for (String month : monthly) {
            MonthlySummary summary = monthlySummaryMap.get(month);
            summary.setTotal(summary.getIncome() - summary.getExpense());
            totalIncome += summary.getIncome();
            totalExpense += summary.getExpense();
        }

        AnnualSummary annualSummary = new AnnualSummary();
        annualSummary.setYear(year);
        annualSummary.setTotalIncome(totalIncome);
        annualSummary.setTotalExpense(totalExpense);
        annualSummary.setTotal(totalIncome - totalExpense);
        annualSummary.setMonthly(monthlySummaryMap);

        return annualSummary;
    }

}
