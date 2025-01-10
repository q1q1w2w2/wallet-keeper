package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.RegularIncome;
import com.project.wallet_keeper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegularIncomeRepository extends JpaRepository<RegularIncome,Integer> {
    List<RegularIncome> findAllByUser(User user);
}
