package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.RegularIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularIncomeRepository extends JpaRepository<RegularIncome,Integer> {
}
