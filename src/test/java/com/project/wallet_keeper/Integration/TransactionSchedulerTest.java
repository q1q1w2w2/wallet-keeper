package com.project.wallet_keeper.Integration;

import com.project.wallet_keeper.dto.category.CreateCategoryDto;
import com.project.wallet_keeper.dto.transaction.TransactionDto;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.entity.*;
import com.project.wallet_keeper.repository.*;
import com.project.wallet_keeper.service.*;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TransactionSchedulerTest {

    @Autowired
    private RegularTransactionService regularTransactionService;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionScheduler transactionScheduler;

//    @Test
    @DisplayName("saveRegularIncomes가 실패해도 saveRegularExpenses는 정상 커밋")
    void testTransactionIsolation() {
        User user = userService.signUp(new SignupDto("test@gmail.com", "1234", "test", LocalDate.now()));
        IncomeCategory incomeCategory = categoryService.createIncomeCategory(new CreateCategoryDto("수입카테고리"));
        ExpenseCategory expenseCategory = categoryService.createExpenseCategory(new CreateCategoryDto("지출카테고리"));

        TransactionDto transactionDtoForIncome = new TransactionDto("수입1", 10000, "", LocalDateTime.now(), incomeCategory.getId());
        TransactionDto transactionDtoForExpense = new TransactionDto("지출1", 10000, "", LocalDateTime.now(), expenseCategory.getId());
        regularTransactionService.saveRegularIncome(user, transactionDtoForIncome);
        regularTransactionService.saveRegularExpense(user, transactionDtoForExpense);

        try {
            transactionScheduler.saveRegularTransactions();
        } catch (Exception e) {
            System.out.println("강제 예외 발생!");
        }

        List<Income> incomes = incomeRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        System.out.println("incomes = " + incomes.size());
        System.out.println("expenses = " + expenses.size());
        assertThat(incomes).isEmpty();
        assertThat(expenses).isNotEmpty();
    }

//    @Test
    @DisplayName("JPA save() vs JdbcTemplate batchUpdate() 성능 비교")
    @Transactional
    void testTransactionIsolationJPA() {
        User user = userService.signUp(new SignupDto("test@gmail.com", "1234", "test", LocalDate.now()));
        IncomeCategory incomeCategory = categoryService.createIncomeCategory(new CreateCategoryDto("수입카테고리"));
        ExpenseCategory expenseCategory = categoryService.createExpenseCategory(new CreateCategoryDto("지출카테고리"));

        List<TransactionDto> incomes = new ArrayList<>();
        List<TransactionDto> expenses = new ArrayList<>();
        for (int i = 0; i < 50_000; i++) {
            TransactionDto transactionDtoForIncome = new TransactionDto("수입" + i, 10000, "", LocalDateTime.now(), incomeCategory.getId());
            TransactionDto transactionDtoForExpense = new TransactionDto("지출" + i, 10000, "", LocalDateTime.now(), expenseCategory.getId());
            regularTransactionService.saveRegularIncome(user, transactionDtoForIncome);
            regularTransactionService.saveRegularExpense(user, transactionDtoForExpense);
            incomes.add(transactionDtoForIncome);
            expenses.add(transactionDtoForExpense);
        }

        long start = System.currentTimeMillis();
        transactionScheduler.saveRegularTransactions();
        long end = System.currentTimeMillis();

        System.out.println("time = " + (end - start) + "ms");
    }
}
