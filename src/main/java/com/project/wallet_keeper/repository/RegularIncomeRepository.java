package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.RegularIncome;
import com.project.wallet_keeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegularIncomeRepository extends JpaRepository<RegularIncome, Long> {
    List<RegularIncome> findAllByUser(User user);
}
