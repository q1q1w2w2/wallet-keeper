package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.ExpenseCategory;
import com.project.wallet_keeper.domain.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    Optional<ExpenseCategory> findByCategoryName(String categoryName);
    List<ExpenseCategory> findAllByIsDeletedFalse();
}
