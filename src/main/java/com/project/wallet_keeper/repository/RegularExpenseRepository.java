package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.RegularExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularExpenseRepository extends JpaRepository<RegularExpense,Integer> {
}
