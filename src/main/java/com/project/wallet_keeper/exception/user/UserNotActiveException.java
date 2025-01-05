package com.project.wallet_keeper.exception.user;

import com.project.wallet_keeper.exception.messages.ErrorMessages;

import static com.project.wallet_keeper.exception.messages.ErrorMessages.*;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException() {
        super(USER_NOT_ACTIVE);
    }
}
