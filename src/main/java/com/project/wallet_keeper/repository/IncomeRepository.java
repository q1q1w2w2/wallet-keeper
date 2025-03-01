package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.dto.transaction.MonthlyIncomeSummary;
import com.project.wallet_keeper.entity.Income;
import com.project.wallet_keeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);

    List<Income> findByUserAndIncomeAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT new com.project.wallet_keeper.dto.transaction.MonthlyIncomeSummary(FUNCTION('DATE_FORMAT', i.incomeAt, '%M'), SUM(i.amount)) " +
            "FROM Income i " +
            "WHERE i.incomeAt BETWEEN :startDate AND :endDate AND i.user = :user " +
            "GROUP BY FUNCTION('DATE_FORMAT', i.incomeAt, '%M')"
    )
    List<MonthlyIncomeSummary> getMonthlyIncomeSummary(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
