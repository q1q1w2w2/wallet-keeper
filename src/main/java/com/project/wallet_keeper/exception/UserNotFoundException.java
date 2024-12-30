package com.project.wallet_keeper.exception;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
