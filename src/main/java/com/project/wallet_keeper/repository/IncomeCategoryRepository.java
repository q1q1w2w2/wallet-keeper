package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {
    List<IncomeCategory> findAllByIsDeletedFalse();
}
