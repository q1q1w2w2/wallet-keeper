package com.project.wallet_keeper.exception.transaction;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class TransactionCategoryNotFoundException extends RuntimeException {
    public TransactionCategoryNotFoundException() {
        super(TRANSACTION_CATEGORY_NOT_FOUND);
    }
}
