package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income,Long> {
}
