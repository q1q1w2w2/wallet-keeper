package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.*;
import com.project.wallet_keeper.dto.transaction.*;
import com.project.wallet_keeper.exception.transaction.InvalidTransactionOwnerException;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.exception.transaction.TransactionNotFoundException;
import com.project.wallet_keeper.repository.*;
import com.project.wallet_keeper.util.websocket.NotificationWebSocketHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    @Transactional
    @CacheEvict(value = "annualSummary", allEntries = true)
    public Income saveIncome(User user, TransactionDto incomeDto) {
        Long categoryId = incomeDto.getTransactionCategoryId();
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        Income income = Income.builder()
                .user(user)
                .detail(incomeDto.getDetail())
                .amount(incomeDto.getAmount())
                .description(Optional.ofNullable(incomeDto.getDescription()).orElse(""))
                .incomeCategory(category)
                .incomeAt(incomeDto.getTransactionAt())
                .build();
        return incomeRepository.save(income);
    }

    @Transactional
    @CacheEvict(value = "annualSummary", allEntries = true)
    public Expense saveExpense(User user, TransactionDto expenseDto) {
        Long categoryId = expenseDto.getTransactionCategoryId();
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        checkBudget(user, expenseDto);

        Expense expense = Expense.builder()
                .user(user)
                .detail(expenseDto.getDetail())
                .amount(expenseDto.getAmount())
                .description(Optional.ofNullable(expenseDto.getDescription()).orElse(""))
                .expenseCategory(category)
                .expenseAt(expenseDto.getTransactionAt())
                .build();
        return expenseRepository.save(expense);
    }

    private void checkBudget(User user, TransactionDto expenseDto) {
        YearMonth yearMonth = YearMonth.from(expenseDto.getTransactionAt());
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        int totalAmount = expenseRepository.getTotalAmountByUserAndDate(user, startDateTime, endDateTime);
        int afterAmount = totalAmount + expenseDto.getAmount();

        if (LocalDateTime.now().getMonth() == yearMonth.getMonth() &&
                LocalDateTime.now().getYear() == yearMonth.getYear()) {
            budgetRepository.findByUserAndYearAndMonth(user, yearMonth.getYear(), yearMonth.getMonthValue())
                    .ifPresent(budget -> {
                        if (budget.getAmount() < afterAmount) {
                            notificationWebSocketHandler.sendNotification("이번 달 예산을 초과하였습니다. 현재 지출: " + afterAmount + "원", user.getId());
                        } else if (budget.getAmount() < afterAmount * 0.8) {
                            notificationWebSocketHandler.sendNotification("경고: 이번 달 예산의 80%를 사용하였습니다. 현재 지출: " + afterAmount + "원", user.getId());
                        }
                    });
        }
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
    @CacheEvict(value = "annualSummary", allEntries = true)
    public Income updateIncome(Long incomeId, TransactionDto incomeDto, User user) {
        Income income = getIncome(incomeId);
        checkTransactionOwnership(income, user);

        IncomeCategory category = incomeCategoryRepository.findById(incomeDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);
        return income.update(incomeDto.getDetail(), incomeDto.getAmount(), incomeDto.getDescription(), incomeDto.getTransactionAt(), category);
    }

    @Transactional
    @CacheEvict(value = "annualSummary", allEntries = true)
    public Expense updateExpense(Long expenseId, TransactionDto expenseDto, User user) {
        Expense expense = getExpense(expenseId);
        checkTransactionOwnership(expense, user);

        ExpenseCategory category = expenseCategoryRepository.findById(expenseDto.getTransactionCategoryId())
                .orElseThrow(TransactionCategoryNotFoundException::new);
        return expense.update(expenseDto.getDetail(), expenseDto.getAmount(), expenseDto.getDescription(), expenseDto.getTransactionAt(), category);
    }

    @Transactional
    @CacheEvict(value = "annualSummary", allEntries = true)
    public void deleteIncome(Long incomeId, User user) {
        Income income = getIncome(incomeId);
        checkTransactionOwnership(income, user);

        incomeRepository.delete(income);
    }

    @Transactional
    @CacheEvict(value = "annualSummary", allEntries = true)
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

        Map<ExpenseCategory, Integer> categoryMap = expenseCategories.stream()
                .collect(Collectors.toMap(category -> category, category -> 0));

        expenseList.forEach(expense ->
                categoryMap.merge(expense.getExpenseCategory(), expense.getAmount(), Integer::sum)
        );

        int totalSpentAmount = categoryMap.values().stream().mapToInt(Integer::intValue).sum();
        return categoryMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new ExpenseSummary(
                        entry.getKey().getId(),
                        entry.getKey().getCategoryName(),
                        entry.getValue(),
                        (int) ((entry.getValue() / (double) totalSpentAmount) * 100))
                )
                .toList();
    }

    @Cacheable(value = "annualSummary", key = "#user.getEmail() + ':' + #year")
    public AnnualSummary getAnnualSummary(User user, int year) {
        LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, 12, 31, 23, 59);

        List<MonthlyIncomeSummary> incomeSummaryList = incomeRepository.getMonthlyIncomeSummary(user, startDateTime, endDateTime);
        List<MonthlyExpenseSummary> expenseSummaryList = expenseRepository.getMonthlyExpenseSummary(user, startDateTime, endDateTime);

        Map<String, MonthlySummary> monthlySummaryMap = Arrays.stream(MonthName.values())
                .collect(Collectors.toMap(MonthName::getName, month -> new MonthlySummary(), (a, b) -> b, LinkedHashMap::new));

        incomeSummaryList.forEach(income -> {
                    String monthKey = MonthName.getMonthName(income.getMonth());
                    monthlySummaryMap.get(monthKey).setIncome(income.getTotalIncome());
                }
        );

        expenseSummaryList.forEach(expense -> {
                    String monthKey = MonthName.getMonthName(expense.getMonth());
                    monthlySummaryMap.get(monthKey).setExpense(expense.getTotalExpense());
                }
        );

        long totalIncome = monthlySummaryMap.values().stream().mapToLong(MonthlySummary::getIncome).sum();
        long totalExpense = monthlySummaryMap.values().stream().mapToLong(MonthlySummary::getExpense).sum();
        monthlySummaryMap.values().forEach(summary ->
                summary.setTotal(summary.getIncome() - summary.getExpense())
        );

        return new AnnualSummary(year, totalIncome, totalExpense, totalIncome - totalExpense, monthlySummaryMap);
    }

    public void generateExcelForTransaction(User user, LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException {
        List<TransactionResponseDto> transactionList = getTransactionList(user, startDate, endDate);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Transactions");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("날짜");
        headerRow.createCell(1).setCellValue("분류");
        headerRow.createCell(2).setCellValue("내용");
        headerRow.createCell(3).setCellValue("금액(원)");
        headerRow.createCell(4).setCellValue("거래 유형");
        headerRow.createCell(5).setCellValue("설명");

        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 28 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        sheet.setColumnWidth(4, 10 * 256);
        sheet.setColumnWidth(5, 30 * 256);

        int rowNum = 1;
        for (TransactionResponseDto transaction : transactionList) {
            LocalDateTime transactionAt = transaction.getTransactionAt();
            Row row = sheet.createRow(rowNum++);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(Date.from(transactionAt.atZone(ZoneId.systemDefault()).toInstant()));
            dateCell.setCellStyle(dateCellStyle);

            row.createCell(1).setCellValue(transaction.getTransactionCategory());
            row.createCell(2).setCellValue(transaction.getDetail());
            row.createCell(3).setCellValue(transaction.getAmount());
            row.createCell(4).setCellValue(transaction.getTransactionType().equals("INCOME") ? "수입" : "지출");
            row.createCell(5).setCellValue(transaction.getDescription() != null ? transaction.getDescription() : "");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
