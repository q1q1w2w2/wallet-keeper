package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.*;
import com.project.wallet_keeper.dto.transaction.RegularTransactionResponseDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.exception.scheduler.SchedulerExecutionException;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.repository.*;
import com.project.wallet_keeper.util.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
    private final NotificationWebSocketHandler notificationWebSocketHandler;

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

    public List<RegularTransactionResponseDto> getRegularTransactions(User user) {
        List<RegularIncome> regularIncomes = regularIncomeRepository.findAllByUser(user);
        List<RegularExpense> regularExpenses = regularExpenseRepository.findAllByUser(user);

        List<RegularTransactionResponseDto> regularTransactions = Stream.concat(
                        regularIncomes.stream().map(RegularTransactionResponseDto::new),
                        regularExpenses.stream().map(RegularTransactionResponseDto::new)
                ).sorted(Comparator.comparing(RegularTransactionResponseDto::getTransactionAt).reversed())
                .toList();
        return regularTransactions;
    }

    @Transactional
    public void deleteRegularIncome(User user, Long regularIncomeId) {
        RegularIncome regularIncome = getRegularIncome(regularIncomeId);
        validateOwnership(user, regularIncome);
        regularIncomeRepository.delete(regularIncome);
    }

    @Transactional
    public void deleteRegularExpense(User user, Long regularExpenseId) {
        RegularExpense regularExpense = getRegularExpense(regularExpenseId);
        validateOwnership(user, regularExpense);
        regularExpenseRepository.delete(regularExpense);
    }

    @Transactional
    public RegularIncome updateRegularIncome(User user, Long regularIncomeId, TransactionDto transactionDto) {
        RegularIncome regularIncome = getRegularIncome(regularIncomeId);

        validateOwnership(user, regularIncome);

        Long categoryId = transactionDto.getTransactionCategoryId();
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return regularIncome.update(transactionDto.getDetail(), transactionDto.getAmount(), transactionDto.getDescription(), transactionDto.getTransactionAt(), category);
    }

    @Transactional
    public RegularExpense updateRegularExpense(User user, Long regularExpenseId, TransactionDto transactionDto) {
        RegularExpense regularExpense = getRegularExpense(regularExpenseId);

        validateOwnership(user, regularExpense);

        Long categoryId = transactionDto.getTransactionCategoryId();
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        return regularExpense.update(transactionDto.getDetail(), transactionDto.getAmount(), transactionDto.getDescription(), transactionDto.getTransactionAt(), category);
    }

    private RegularIncome getRegularIncome(Long regularIncomeId) {
        return regularIncomeRepository.findById(regularIncomeId)
                .orElseThrow(TransactionNotFoundException::new);
    }

    private RegularExpense getRegularExpense(Long regularExpenseId) {
        return regularExpenseRepository.findById(regularExpenseId)
                .orElseThrow(TransactionNotFoundException::new);
    }

    private void validateOwnership(User user, Transaction transaction) {
        if (!transaction.getUser().equals(user)) {
            throw new InvalidTransactionOwnerException();
        }
    }

    public List<Income> saveRegularIncomes() {
        List<RegularIncome> regularIncomes = regularIncomeRepository.findAll();
        LocalDate now = LocalDate.now();

        List<Income> incomesToSave = new ArrayList<>();
        for (RegularIncome regularIncome : regularIncomes) {
            LocalDateTime incomeAt = regularIncome.getIncomeAt();
            int dayOfMonth = incomeAt.getDayOfMonth();

            LocalDateTime nextIncomeDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), dayOfMonth, 0, 0);

            Income income = Income.builder()
                    .user(regularIncome.getUser())
                    .detail(regularIncome.getDetail())
                    .amount(regularIncome.getAmount())
                    .description(regularIncome.getDescription() != null ? regularIncome.getDescription() : "")
                    .incomeCategory(regularIncome.getIncomeCategory())
                    .incomeAt(nextIncomeDateTime)
                    .build();
            incomesToSave.add(income);
        }
        return incomeRepository.saveAll(incomesToSave);
    }

    public List<Expense> saveRegularExpenses() {
        List<RegularExpense> regularExpenses = regularExpenseRepository.findAll();
        LocalDate now = LocalDate.now();

        List<Expense> expensesToSave = new ArrayList<>();
        for (RegularExpense regularExpense : regularExpenses) {
            LocalDateTime incomeAt = regularExpense.getExpenseAt();
            int dayOfMonth = incomeAt.getDayOfMonth();

            LocalDateTime nextIncomeDateTime = LocalDateTime.of(now.getYear(), now.getMonth(), dayOfMonth, 0, 0);

            Expense expense = Expense.builder()
                    .user(regularExpense.getUser())
                    .detail(regularExpense.getDetail())
                    .amount(regularExpense.getAmount())
                    .description(regularExpense.getDescription() != null ? regularExpense.getDescription() : "")
                    .expenseCategory(regularExpense.getExpenseCategory())
                    .expenseAt(nextIncomeDateTime)
                    .build();
            expensesToSave.add(expense);
        }
        return expenseRepository.saveAll(expensesToSave);
    }

    @Transactional
    @Scheduled(cron = "0 0 4 1 * ?")
    public void saveRegularTransactions() {
        try {
            List<Income> savedIncomes = saveRegularIncomes();
            List<Expense> savedExpenses = saveRegularExpenses();
            int totalSavedTransactions = savedIncomes.size() + savedExpenses.size();

            log.info("정기 거래 저장 스케줄러 실행됨: {}", LocalDateTime.now());
            if (!savedIncomes.isEmpty() || !savedExpenses.isEmpty()) {
                notificationWebSocketHandler.sendNotification(totalSavedTransactions + "개의 정기 거래가 저장되었습니다. 새로고침 해주세요.");
            }
        } catch (Exception e) {
            throw new SchedulerExecutionException("정기 거래 저장 스케줄러 실행 중 오류 발생");
        }
    }
}