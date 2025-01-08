package com.project.wallet_keeper.exception.transaction;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class CategoryAlreadyExistException extends RuntimeException {
    public CategoryAlreadyExistException() {
        super(CATEGORY_ALREADY_EXIST);
    }
}
