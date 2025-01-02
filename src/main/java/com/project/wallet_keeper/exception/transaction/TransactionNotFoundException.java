package com.project.wallet_keeper.exception.transaction;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super(TRANSACTION_NOT_FOUND);
    }
}
