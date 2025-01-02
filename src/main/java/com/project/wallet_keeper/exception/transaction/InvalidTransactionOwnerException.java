package com.project.wallet_keeper.exception.transaction;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class InvalidTransactionOwnerException extends RuntimeException {
    public InvalidTransactionOwnerException() {
        super(INVALID_TRANSACTION_OWNER);
    }
}
