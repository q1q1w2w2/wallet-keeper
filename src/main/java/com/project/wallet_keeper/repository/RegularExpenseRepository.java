package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.entity.RegularExpense;
import com.project.wallet_keeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegularExpenseRepository extends JpaRepository<RegularExpense, Long> {
    List<RegularExpense> findAllByUser(User user);
}
