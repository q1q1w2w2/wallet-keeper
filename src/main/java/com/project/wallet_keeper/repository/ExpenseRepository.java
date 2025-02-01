package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.Expense;
import com.project.wallet_keeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

    List<Expense> findByUserAndExpenseAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user = :user and e.expenseAt between :start and :end")
    int getTotalAmountByUserAndDate(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
