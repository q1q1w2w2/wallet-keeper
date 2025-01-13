package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.Budget;
import com.project.wallet_keeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndYearAndMonth(User user, int year, int month);
}
