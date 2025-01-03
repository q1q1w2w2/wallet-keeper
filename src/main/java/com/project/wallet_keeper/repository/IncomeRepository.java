package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income,Long> {
    List<Income> findByUser(User user);
    List<Income> findByUserAndIncomeAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
