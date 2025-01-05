package com.project.wallet_keeper.repository;

import com.project.wallet_keeper.domain.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Integer> {

}
