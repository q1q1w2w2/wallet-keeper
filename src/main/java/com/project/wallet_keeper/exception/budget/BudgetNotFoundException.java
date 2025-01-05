package com.project.wallet_keeper.exception.budget;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException() {
        super(BUDGET_NOT_FOUNT);
    }
}
