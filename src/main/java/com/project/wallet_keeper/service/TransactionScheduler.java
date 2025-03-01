package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.RegularExpense;
import com.project.wallet_keeper.entity.RegularIncome;
import com.project.wallet_keeper.exception.scheduler.SchedulerExecutionException;
import com.project.wallet_keeper.repository.RegularExpenseRepository;
import com.project.wallet_keeper.repository.RegularIncomeRepository;
import com.project.wallet_keeper.util.event.SaveRegularExpensesEvent;
import com.project.wallet_keeper.util.event.SaveRegularIncomesEvent;
import com.project.wallet_keeper.util.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionScheduler {

    private final RegularIncomeRepository regularIncomeRepository;
    private final RegularExpenseRepository regularExpenseRepository;
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Scheduled(cron = "0 0 4 1 * ?")
    public void saveRegularTransactions() {
        try {
            eventPublisher.publishEvent(new SaveRegularIncomesEvent(this));
            eventPublisher.publishEvent(new SaveRegularExpensesEvent(this));

            List<RegularIncome> regularIncomes = regularIncomeRepository.findAll();
            List<RegularExpense> regularExpenses = regularExpenseRepository.findAll();

            log.info("정기 거래 저장 스케줄러 실행됨: {}", LocalDateTime.now());

            Set<Long> userIds = new HashSet<>();

            regularIncomes.forEach(income -> userIds.add(income.getUser().getId()));
            regularExpenses.forEach(expense -> userIds.add(expense.getUser().getId()));

            for (Long userId : userIds) {
                notificationWebSocketHandler.sendNotification("정기 거래가 저장되었습니다. 새로고침 해주세요.", userId);
            }
        } catch (Exception e) {
            throw new SchedulerExecutionException("정기 거래 저장 스케줄러 실행 중 오류 발생");
        }
    }
}
