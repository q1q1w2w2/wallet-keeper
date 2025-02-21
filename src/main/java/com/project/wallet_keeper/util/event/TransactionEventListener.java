package com.project.wallet_keeper.util.event;

import com.project.wallet_keeper.service.TransactionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionScheduler transactionScheduler;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSaveRegularIncomes(SaveRegularIncomesEvent event) {
        try {
            transactionScheduler.saveRegularIncomes();
        } catch (Exception e) {
            log.error("saveRegularIncomes error", e.getMessage());
        }
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSaveRegularExpenses(SaveRegularExpensesEvent event) {
        try {
            transactionScheduler.saveRegularExpenses();
        } catch (Exception e) {
            log.error("saveRegularExpenses error", e.getMessage());
        }
    }
}
