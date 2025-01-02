package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.Income;
import com.project.wallet_keeper.domain.IncomeCategory;
import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.transaction.SaveIncomeDto;
import com.project.wallet_keeper.exception.transaction.TransactionCategoryNotFoundException;
import com.project.wallet_keeper.repository.IncomeCategoryRepository;
import com.project.wallet_keeper.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    @Transactional
    public Income save(User user, SaveIncomeDto incomeDto) {
        Long categoryId = incomeDto.getIncomeCategoryId();
        IncomeCategory category = incomeCategoryRepository.findById(categoryId)
                .orElseThrow(TransactionCategoryNotFoundException::new);

        Income income = Income.builder()
                .user(user)
                .detail(incomeDto.getDetail())
                .amount(incomeDto.getAmount())
                .description(incomeDto.getDescription() != null ? incomeDto.getDescription() : "")
                .incomeCategory(category)
                .incomeAt(incomeDto.getIncomeAt())
                .build();
        return incomeRepository.save(income);
    }
}
