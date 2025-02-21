package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.Expense;
import com.project.wallet_keeper.entity.Income;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BatchInsertRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveIncomes(List<Income> incomes) {
        log.info("[Batch Insert 시작] {}개의 Income 저장 중...", incomes.size());
        String sql = "INSERT INTO income (detail, amount, description, user_id, income_at, income_category_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, incomes, incomes.size(),
                    (PreparedStatement ps, Income income) -> {
                        LocalDateTime now = LocalDateTime.now();

                        ps.setString(1, income.getDetail());
                        ps.setInt(2, income.getAmount());
                        ps.setString(3, income.getDescription());
                        ps.setLong(4, income.getUser().getId());
                        ps.setTimestamp(5, Timestamp.valueOf(income.getIncomeAt()));
                        ps.setLong(6, income.getIncomeCategory().getId());
                        ps.setTimestamp(7, Timestamp.valueOf(now));
                        ps.setTimestamp(8, Timestamp.valueOf(now));
                    }
            );
            log.info("[Batch Insert 완료] Income 저장 완료!");
        } catch (Exception e) {
            log.error("Batch Insert 중 예외 발생: {}", e);
            throw e;
        }
    }

    public void saveExpenses(List<Expense> expenses) {
        log.info("[Batch Insert 시작] {}개의 Expense 저장 중...", expenses.size());
        String sql = "INSERT INTO expense (detail, amount, description, user_id, expense_at, expense_category_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, expenses, expenses.size(),
                (PreparedStatement ps, Expense expense) -> {
                    LocalDateTime now = LocalDateTime.now();

                    ps.setString(1, expense.getDetail());
                    ps.setInt(2, expense.getAmount());
                    ps.setString(3, expense.getDescription());
                    ps.setLong(4, expense.getUser().getId());
                    ps.setTimestamp(5, Timestamp.valueOf(expense.getExpenseAt()));
                    ps.setLong(6, expense.getExpenseCategory().getId());
                    ps.setTimestamp(7, Timestamp.valueOf(now));
                    ps.setTimestamp(8, Timestamp.valueOf(now));
                }
        );
        log.info("[Batch Insert 완료] Expense 저장 완료!");
    }
}
