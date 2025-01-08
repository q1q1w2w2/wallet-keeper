package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {
    Optional<IncomeCategory> findByCategoryName(String categoryName);
    List<IncomeCategory> findAllByIsDeletedFalse();
}
